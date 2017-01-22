package rs.townhall.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import rs.townhall.dao.interceptor.AuthenticationInterceptor;
import rs.townhall.entity.IDGenerator;
import rs.townhall.entity.amendment.Amandman;
import rs.townhall.security.LoadDocument;
import rs.townhall.security.SignEnveloped;
import util.MetadataExtractor;
import util.MyValidationEventHandler;
import util.NSPrefixMapper;
import util.Util;
import util.Util.ConnectionProperties;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.ResourceNotFoundException;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

@Stateless
@Local(AmendmentsDao.class)
@Interceptors(AuthenticationInterceptor.class)
public class AmendmentsDaoBean implements AmendmentsDao {

	private static DatabaseClient client;
	private static JAXBContext context;
	private static Unmarshaller unmarshaller;
	private static Marshaller marshaller;
	private static Schema schema;
	private static DocumentMetadataHandle metadata;
	private static final String TOMEE_ROOT = System.getProperty("catalina.base");
	private static final String DOCUMENT = "/_data/documents/";
	private static final String SCHEMA = "/_data/schemas/amandman.xsd";
	private static final String SPARQL_AMAND_GRAPH_URI = "amendment/metadata";
	private static GraphManager graphManager;
	private static MetadataExtractor extractor;
	private static TransformerFactory transformerFactory;
	private static final String COLLECTION = "amandmani/";
	private static Amandman amandman;
	private static List<Amandman> amandmani;
	private static QueryManager queryManager;
	private static final String PRIVATE_KEY = "/_data/security/arhiv.key";
	private static final String KEY_STORE_FILE = "/_data/security/odbornici.jks";	
	private static final String PROPERTY = "<http://www.parlament.gov.rs/predicate/";

	
	static {
		// Setting connection properties
		ConnectionProperties props;
		transformerFactory.newInstance();
		
		try {
			props = Util.loadProperties();
			System.out.println("[INFO] Using \"" + props.database + "\" database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password,
					props.authType);
			queryManager = client.newQueryManager();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Setting schema/metadata
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			schema = schemaFactory.newSchema(new File(TOMEE_ROOT + SCHEMA));
		} catch (SAXException e) {
			e.printStackTrace();
		}
		metadata = new DocumentMetadataHandle();
		metadata.getCollections().add("amandmani/");
		// Setting marshaller/unmarshaller
		try {
			context = JAXBContext.newInstance(Amandman.class);
			unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new MyValidationEventHandler());
			marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.parlament.gov.rs/amandman file:/" + TOMEE_ROOT + SCHEMA);
			graphManager = client.newGraphManager();
			graphManager.setDefaultMimetype(RDFMimeTypes.RDFXML);
			extractor = new MetadataExtractor();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	@Override
	public Amandman findById(String id) {
		List<Amandman> list = findAll();
		for (Amandman amendment : list){
			if (amendment.getId().equals(id)){
				return amendment;
			}
		}
		
		return null;
	}

	@Override
	public List<Amandman> findAll() {
		List<Amandman> amendments = new ArrayList<Amandman>();
		
		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery("for $doc in fn:collection('amandmani/') return $doc");
		
		EvalResultIterator response = invoker.eval();
		if (response.hasNext()) {
			for (EvalResult result : response) {
				try {
					Unmarshaller _unmarshaller = context.createUnmarshaller();
					Amandman amendment = (Amandman) _unmarshaller.unmarshal(new StringReader(result.getString()));
					amendments.add(amendment);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("There is not result");	
		}
		
		return amendments;
	}

	@Override
	public Amandman remove(String id) {
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		
		Amandman amendment = findById(id);
		if (amendment != null)
			xmlManager.delete(id);
		
		// Return deleted amendment
		return amendment;
	}

	@Override
	public Amandman persist(Amandman amendment) {
		
		if (amendment.getId() == null)
			amendment.setId(IDGenerator.generateID());
		
		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery("declare namespace am = 'http://www.parlament.gov.rs/amandman';\n"
						+"declare namespace pr = 'http://www.parlament.gov.rs/propisi';\n"
						+"for $doc in fn:collection('propisi/')/pr:propis\n"
						+"where $doc//pr:clan[@ID='"+amendment.getOdredba_predloga()+"']\n"
						+ "or $doc//pr:odeljak[@ID='"+amendment.getOdredba_predloga()+"']\n"
						+ "or $doc[@ID=\'"+amendment.getOdredba_predloga()+"']\n" 
						+ "return $doc");
		
		EvalResultIterator response = invoker.eval();
		if (response.hasNext()) {
					
			amendment.setAbout("http://www.parlament.gov.rs/amandmani/"+amendment.getId());
			amendment.getNaziv_predloga().setProperty("pred:naziv_predloga");
			amendment.getNaziv_predloga().setDatatype("xs:string");;
			amendment.getDatum_donosenja().setProperty("pred:datum_donosenja");
			amendment.getDatum_donosenja().setDatatype("xs:date");
			amendment.getBroj_glasova_protiv().setProperty("pred:broj_glasova_protiv");
			amendment.getBroj_glasova_protiv().setDatatype("xs:int");
			amendment.getBroj_glasova_za().setProperty("pred:broj_glasova_za");
			amendment.getBroj_glasova_za().setProperty("xs:int");
			amendment.getMesto().setProperty("pred:mesto");
			amendment.getMesto().setDatatype("xs:string");
			amendment.getDatum_kreiranja().setProperty("pred:datum_kreiranja");
			amendment.getDatum_kreiranja().setDatatype("xs:date");
			
			String fileName = amendment.getId();
			XMLDocumentManager docManager = client.newXMLDocumentManager();
			File file = new File(TOMEE_ROOT + DOCUMENT + fileName + ".xml");
			try {
				
				marshaller.marshal(amendment, file);
				
				InputStreamHandle handle = new InputStreamHandle(new FileInputStream(TOMEE_ROOT + DOCUMENT + fileName + ".xml"));
	
				//potpisivanje dokumenta
				Document doc = LoadDocument.loadDocument(file);
				PrivateKey pk = SignEnveloped.readPrivateKey(TOMEE_ROOT + PRIVATE_KEY);
				Certificate cert = SignEnveloped.readCertificate(TOMEE_ROOT + KEY_STORE_FILE,"arhiv","kr");
				Document docSigned = SignEnveloped.signDocument(doc, pk, cert);
				
				if (SignEnveloped.verifySignature(docSigned)){
					LoadDocument.saveDocument(docSigned, file);
					extractor.extractMetadata(
							new FileInputStream(file), 
							new FileOutputStream(new File(fileName + ".rdf")));
							
					FileHandle rdfFileHandle =
							new FileHandle(new File(fileName + ".rdf"))
							.withMimetype(RDFMimeTypes.RDFXML);
					
					graphManager.write(SPARQL_AMAND_GRAPH_URI, rdfFileHandle);	
					
					docManager.write(fileName, metadata, handle);
					return amendment;
				}else{
					throw new Exception("Failed to sign amendment");
				}
					
				
			} catch (JAXBException | FileNotFoundException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
	} else {
		try {
			//throw new Exception("There is no regulation that is equal with proposition");
			throw new ResourceNotFoundException("There is no regulation that is equal with proposition");
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
		return null;
	}
	
	
	@Override
	public List<Amandman> findBySearch(String search){
		amandman = new Amandman();
		amandmani = new ArrayList<Amandman>();
		StringQueryDefinition queryDefinition = queryManager.newStringDefinition();
		queryDefinition.setCriteria(search);
		queryDefinition.setCollections(COLLECTION);
		
		SearchHandle results = queryManager.search(queryDefinition, new SearchHandle());
				
		MatchDocumentSummary matches[] = results.getMatchResults();
		MatchDocumentSummary result;
		System.out.println(matches.length);
		if (matches.length>0){
			for (int i = 0; i < matches.length; i++) {
				result = matches[i];
				System.out.println(result.getUri());
				amandman = findById(result.getUri());
				amandmani.add(amandman);
			}
			return amandmani;
		}else{
			return null;
		}
	}
	
	public String getSuggestedAmendments(String query)
	{
		System.out.println("Get suggested amendments..");
		String list ="";
		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery(query);

		EvalResultIterator response = invoker.eval();
		if (response.hasNext()) {
			for (EvalResult result : response) {
				list+= result.getString();
			}
		}
		return list;
	}

	@Override
	public List<Amandman> findByAdvancedSearch(String search){
		HashMap<String,String> elementiZaPretragu = new HashMap<String,String>();
		Map<String, String> map = new HashMap<String, String>();
		
		String[] params = search.split("&");   
	    for (String param : params)  
	    {  
	        String name = param.split("=")[0];  
	        String value = param.split("=")[1];  
	        map.put(name, value);  
	    }  
	    
	    String datum_donosenja = map.get("datum_donosenja");
	    String datum_kreiranja = map.get("datum_kreiranja");
	    String mesto = map.get("mesto");
	    String broj_glasova_protiv = map.get("broj_glasova_protiv");
	    String broj_glasova_za = map.get("broj_glasova_za");
	    String naziv_predloga = map.get("naziv_predloga");
	    
		if (datum_donosenja!=""){
			System.out.println("opet");
			elementiZaPretragu.put("datum_donosenja", "\"" + datum_donosenja + "\"^^xs:date");
		}
		if (datum_kreiranja!=""){
			elementiZaPretragu.put("datum_kreiranja", "\"" + datum_kreiranja + "\"^^xs:date");
		}
		if(mesto!=""){
			elementiZaPretragu.put("mesto", "\"" + mesto + "\"^^xs:string");
		}
		if(broj_glasova_protiv!=""){
			elementiZaPretragu.put("broj_glasova_protiv", "\"" + broj_glasova_protiv + "\"^^xs:int");
		}
		if(broj_glasova_za!=""){
			elementiZaPretragu.put("broj_glasova_za", "\"" + broj_glasova_za + "\"^^xs:int");
		}
		if(naziv_predloga!=""){
			elementiZaPretragu.put("naziv_predloga", "\"" + naziv_predloga + "\"^^xs:string");
		}
		
		System.out.println(elementiZaPretragu);
		
		StringBuilder query = new StringBuilder();
		query.append("PREFIX xs: <http://www.w3.org/2001/XMLSchema#>\n");
		query.append("SELECT ?amandman FROM <amendment/metadata>\n");
		query.append("WHERE{\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"datum_donosenja> ?datum_donosenja .\n}\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"datum_kreiranja> ?datum_kreiranja .\n}\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"broj_glasova_za> ?broj_glasova_za .\n}\n");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"broj_glasova_protiv> ?broj_glasova_protiv .\n}");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"naziv_predloga> ?naziv_predloga .\n}");
		query.append("OPTIONAL{\n ?amandman " + PROPERTY+"mesto> ?mesto .\n}");
		
		
		StringBuilder filter = new StringBuilder();
		filter.append("FILTER (\n");
		Iterator<HashMap.Entry<String, String>> elementiIter = elementiZaPretragu.entrySet().iterator();
		while (elementiIter.hasNext()) {
		    HashMap.Entry<String, String> element = elementiIter.next();
		      	filter.append(" ?"+element.getKey() + " = " + element.getValue());
		      	if (elementiIter.hasNext()){
		      		filter.append(" , ");
		      	}else{
		      		filter.append(")\n}");    
		      	}
		}
		
		if (elementiZaPretragu.isEmpty()){
			query.append("}");
		}else{
			query.append(filter);
		}
	

		SPARQLQueryManager sparqlQueryManager = client.newSPARQLQueryManager();
		
		JacksonHandle resultsHandle = new JacksonHandle();
		resultsHandle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);
		
		SPARQLQueryDefinition queryDef = sparqlQueryManager.newQueryDefinition(query.toString());
		
		resultsHandle = sparqlQueryManager.executeSelect(queryDef, resultsHandle);	
		
		List<JsonNode> tuples = resultsHandle.get().findValues("value");
		for ( JsonNode am : tuples ) {
			String[] a=am.asText().split("/");
			amandmani.add(findById(a[a.length-1]));
		}
	
		return amandmani;
	}
}