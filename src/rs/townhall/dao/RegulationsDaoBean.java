package rs.townhall.dao;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.crypto.SecretKey;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import rs.townhall.dao.interceptor.AuthenticationInterceptor;
import rs.townhall.entity.regulation.Propis;
import rs.townhall.security.EncryptKEK;
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
@Local(RegulationsDao.class)
@Interceptors(AuthenticationInterceptor.class)
public class RegulationsDaoBean implements RegulationsDao {

	private static DatabaseClient client;
	private static JAXBContext context;
	private static Unmarshaller unmarshaller;
	private static Marshaller marshaller;
	private static Schema schema;
	private static DocumentMetadataHandle metadata;
	private static final String TOMEE_ROOT = System.getProperty("catalina.base");
	private static final String PDF = "/_data/pdf/";
	private static final String DOCUMENT = "/_data/documents/";
	private static final String SCHEMA = "/_data/schemas/propis.xsd";
	private static final String SPARQL_AMAND_GRAPH_URI = "regulation/metadata";	
	private static GraphManager graphManager;
	private static MetadataExtractor extractor;
	private static final String COLLECTION = "propisi/";
	private static Propis propis;
	private static List<Propis> propisi;
	private static QueryManager queryManager;
	private static final String PRIVATE_KEY = "/_data/security/arhiv.key";
	private static final String KEY_STORE_FILE = "/_data/security/odbornici.jks";	
	private static final String PROPERTY = "<http://www.parlament.gov.rs/predicate/";
	private static FopFactory fopFactory;
	private static TransformerFactory transformerFactory;
		

	static {
		// Setting connection properties
		ConnectionProperties props;
		
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
		metadata.getCollections().add("propisi/");
		// Setting marshaller/unmarshaller
		try {
			context = JAXBContext.newInstance(Propis.class);
			unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new MyValidationEventHandler());
			marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.parlament.gov.rs/propisi file:/" + TOMEE_ROOT + SCHEMA);
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
		
		// Pdf
		try { 
			fopFactory = FopFactory.newInstance(new File(TOMEE_ROOT + PDF + "fop.xconf"));
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
		transformerFactory = new TransformerFactoryImpl();
	}

	@Override
	public Propis findById(String id) {
		List<Propis> regulations = new ArrayList<Propis>();
		regulations = findAll();
		for (Propis regulation : regulations) {
			if (regulation.getId().equals(id)) {
				return regulation;
			}
		}
		return null;
	}

	@Override
	public List<Propis> findAll() {
		List<Propis> regulations = new ArrayList<Propis>();

		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery("for $doc in fn:collection('propisi/') return $doc");

		EvalResultIterator response = invoker.eval();
		if (response.hasNext()) {
			for (EvalResult result : response) {
				try {
					Unmarshaller _unmarshaller = context.createUnmarshaller();
					Propis p = (Propis) _unmarshaller.unmarshal(new StringReader(result.getString()));
					if (!p.getId().startsWith("acrhieve_"))
						regulations.add(p);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("There is not result");
		}
		// TODO: Fix problem with releasing client..
		// client.release();

		return regulations;
	}

	@Override
	public Propis remove(String id) {
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		Propis regulation = findById(id);
		if (regulation != null)
			xmlManager.delete(id);

		// Return deleted regulation
		return regulation;
	}

	@Override
	public Propis persist(Propis regulation) {
		/*
		if (regulation.getId() == null)
			regulation.setId(IDGenerator.generateID());
		*/
		
		regulation.populateIds();
		
		regulation.setAbout("http://www.parlament.gov.rs/propisi/"+regulation.getId());
		regulation.getDatum_donosenja().setProperty("pred:datum_donosenja");
		regulation.getDatum_donosenja().setDatatype("xs:date");
		regulation.getMesto().setProperty("pred:mesto");
		regulation.getMesto().setDatatype("xs:string");
		regulation.getNaziv().setProperty("pred:naziv");
		regulation.getNaziv().setDatatype("xs:string");
		
		String fileName = regulation.getId();
		XMLDocumentManager docManager = client.newXMLDocumentManager();
		File file = new File(TOMEE_ROOT + DOCUMENT + fileName + ".xml");
		try {
			marshaller.marshal(regulation, file);
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
				return regulation;
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

		return null;
	}

	@Override
	public boolean archive(String id) {
		Propis regulation = findById(id);
		if (regulation != null) {

			StringWriter sw = new StringWriter();
			try {
				marshaller.marshal(regulation, sw);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			
			String xmlString = sw.toString();
			Document doc = LoadDocument.convertStringToDocument(xmlString);
			SecretKey secretKey = EncryptKEK.generateDataEncryptionKey();
			Certificate cert = EncryptKEK.readCertificate(TOMEE_ROOT + KEY_STORE_FILE, "arhiv", "kr");
			Document encriptedDoc = EncryptKEK.encrypt(doc ,secretKey, cert, "propis");
			xmlString = LoadDocument.documentToString(encriptedDoc);

			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost("http://localhost:8080/historical-arcieve/archive/regulation");
				post.setHeader("Content-Type", "application/xml");
				HttpEntity entity = new ByteArrayEntity(xmlString.getBytes("UTF-8"));
				post.setEntity(entity);

				HttpResponse response = client.execute(post);
				// Return true if success
				if (response.getStatusLine().getStatusCode() == 200)
					return true;
				else
					return false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public List<Propis> findBySearch(String search){
		propis = new Propis();
		propisi = new ArrayList<Propis>();
		StringQueryDefinition queryDefinition = queryManager.newStringDefinition();
		queryDefinition.setCriteria(search);
		queryDefinition.setCollections(COLLECTION);
		
		SearchHandle results = queryManager.search(queryDefinition, new SearchHandle());
				
		MatchDocumentSummary matches[] = results.getMatchResults();
		MatchDocumentSummary result;
		
		System.out.println(matches.length);
		if(matches.length>0){
			for (int i = 0; i < matches.length; i++) {
				result = matches[i];
				System.out.println(result.getUri());
				propis = findById(result.getUri());
				propisi.add(propis);
			}
			return propisi;
		}else{
			return null;
		}
	}
	
	public String getSectionIds(String id, String query)
	{
		System.out.println("Looking for sections of regulation with id "+id);
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
	
	public String getArticleIds(String id, String query)
	{
		System.out.println("Looking for articles of regulation with id "+id);
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
	public List<Propis> findByAdvancedSearch(String search){
		HashMap<String,String> elementiZaPretragu = new HashMap<String,String>();
		
	/*	if (datum_donosenja!=""){
			elementiZaPretragu.put("datum_donosenja", "\"" + datum_donosenja + "\"^^xs:date");
		}
		if(mesto!=""){
			elementiZaPretragu.put("mesto", "\"" + mesto + "\"^^xs:string");
		}
		if(naziv!=""){
			elementiZaPretragu.put("naziv", "\"" + naziv + "\"^^xs:string");
		}
		*/
		StringBuilder query = new StringBuilder();
		query.append("PREFIX xs: <http://www.w3.org/2001/XMLSchema#>\n");
		query.append("SELECT ?propis FROM <regulation/metadata>\n");
		query.append("WHERE{\n");
		query.append("OPTIONAL{\n ?propis " + PROPERTY+"datum_donosenja> ?datum_donosenja .\n}\n");
		query.append("OPTIONAL{\n ?propis " + PROPERTY+"naziv> ?naziv .\n}");
		query.append("OPTIONAL{\n ?propis " + PROPERTY+"mesto> ?mesto .\n}");
		
		
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
			propisi.add(findById(a[a.length-1]));
		}
	
		return propisi;
	}
	

	public String getSuggestedRegulations(String query)
	{
		System.out.println("Get suggested regulations..");
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
	public void generatePdf(String id) {
		String XML_FILE_NAME = id + ".xml";
		String PDF_FILE_NAME = id + ".pdf";
		Propis regulation = this.findById(id);
		// Save curent regulation as xml file on TomEE
        try {
			marshaller.marshal(regulation, new File(TOMEE_ROOT + DOCUMENT + XML_FILE_NAME));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
        File xsltFile = new File(TOMEE_ROOT + PDF + "propis.xsl");
		StreamSource transformSource = new StreamSource(xsltFile);
		StreamSource source = new StreamSource(new File(TOMEE_ROOT + DOCUMENT + XML_FILE_NAME));
		FOUserAgent userAgent = fopFactory.newFOUserAgent();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Transformer xslFoTransformer;
		try {
			xslFoTransformer = transformerFactory.newTransformer(transformSource);
			Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, outStream);
			Result res = new SAXResult(fop.getDefaultHandler());
			xslFoTransformer.transform(source, res);

			File pdfFile = new File(TOMEE_ROOT + PDF + PDF_FILE_NAME);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(pdfFile));
			out.write(outStream.toByteArray());
			out.close();
		} catch (FOPException | TransformerException | IOException e) {
			e.printStackTrace();
		}
		
	}

}