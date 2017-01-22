package rs.townhall.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import rs.townhall.dao.interceptor.AuthenticationInterceptor;
import rs.townhall.entity.IDGenerator;
import rs.townhall.entity.session.Sednica;
import util.MyValidationEventHandler;
import util.NSPrefixMapper;
import util.Util;
import util.Util.ConnectionProperties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

@Stateless
@Local(SessionDao.class)
@Interceptors(AuthenticationInterceptor.class)
public class SessionDaoBean implements SessionDao{
	
	private static DatabaseClient client;
	private static JAXBContext context;
	private static Unmarshaller unmarshaller;
	private static Marshaller marshaller;
	private static Schema schema;
	private static DocumentMetadataHandle metadata;
	private static final String TOMEE_ROOT = System.getProperty("catalina.base");
	private static final String DOCUMENT = "/_data/documents/";
	private static final String SCHEMA = "/_data/schemas/sednica.xsd";
	private static final String KEY_STORE_FILE = "/_data/security/odbornici.jks";
	private static final String SPARQL_AMAND_GRAPH_URI = "regulation/metadata";	
	
	static {
		// Setting connection properties
		ConnectionProperties props;
		
		try {
			props = Util.loadProperties();
			System.out.println("[INFO] Using \"" + props.database + "\" database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password,
					props.authType);
	
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
		metadata.getCollections().add("sednice/");
		// Setting marshaller/unmarshaller
		try {
			context = JAXBContext.newInstance(Sednica.class);
			unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new MyValidationEventHandler());
			marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.parlament.gov.rs/sednica file:/" + TOMEE_ROOT + SCHEMA);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Sednica findById(String id) {
		List<Sednica> sessions = new ArrayList<Sednica>();
		sessions = findAll();
		for (Sednica session : sessions) {
			if (session.getId().equals(id)) {
				return session;
			}
		}

		return null;
	}

	@Override
	public List<Sednica> findAll() {
		List<Sednica> sessions = new ArrayList<Sednica>();

		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery("for $doc in fn:collection('sednice/') return $doc");

		EvalResultIterator response = invoker.eval();
		if (response.hasNext()) {
			for (EvalResult result : response) {
				try {
					Unmarshaller _unmarshaller = context.createUnmarshaller();
					Sednica s = (Sednica) _unmarshaller.unmarshal(new StringReader(result.getString()));
					sessions.add(s);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("There is not result");
		}
		
		return sessions;
	}

	@Override
	public Sednica remove(String id) {
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();

		Sednica session = findById(id);
		if (session != null)
			xmlManager.delete(id);

		// Return deleted regulation
		return session;
	}

	@Override
	public Sednica persist(Sednica session) {
		
		if (session.getId() == null)
			session.setId(IDGenerator.generateID());
		
		String fileName = session.getId();
		XMLDocumentManager docManager = client.newXMLDocumentManager();
		File file = new File(TOMEE_ROOT + DOCUMENT + fileName + ".xml");
		try {
			marshaller.marshal(session, file);
			InputStreamHandle handle = new InputStreamHandle(new FileInputStream(TOMEE_ROOT + DOCUMENT + fileName + ".xml"));
			docManager.write(fileName, metadata, handle);
			
			return session;
		} catch (JAXBException | FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public String setSessionState(String query)
	{
		System.out.println("Change state..");
		String res ="";
		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery(query);
		invoker.eval();
		
		return res;
	}
	
	public String setTimetable(String query)
	{
		System.out.println("Add timetable..");
		String res ="";
		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery(query);
		invoker.eval();
		
		return res;
	}
}