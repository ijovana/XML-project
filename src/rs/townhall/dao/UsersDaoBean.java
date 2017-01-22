package rs.townhall.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
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
import rs.townhall.entity.user.Korisnik;
import rs.townhall.rest.model.AuthenticationData;
import rs.townhall.security.Hash;
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
@Local(UsersDao.class)
@Interceptors(AuthenticationInterceptor.class)
public class UsersDaoBean implements UsersDao {

	private static DatabaseClient client;
	private static JAXBContext context;
	private static Unmarshaller unmarshaller;
	private static Marshaller marshaller;
	private static Schema schema;
	private static DocumentMetadataHandle metadata;
	private static final String TOMEE_ROOT = System.getProperty("catalina.base");
	private static final String DOCUMENT = "/_data/documents/";
	private static final String SCHEMA = "/_data/schemas/korisnik.xsd";

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
		metadata.getCollections().add("korisnici/");
		// Setting marshaller/unmarshaller
		try {
			context = JAXBContext.newInstance(Korisnik.class);
			unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			unmarshaller.setEventHandler(new MyValidationEventHandler());
			marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.parlament.gov.rs/korisnici file:/" + TOMEE_ROOT + SCHEMA);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}	
	
	@Override
	public Korisnik findById(String id){
		List<Korisnik> users = findAll();
		for (Korisnik user : users){
			if (user.getId().equals(id)){
				return user;
			}
		}
		
		return null;
	}

	@Override
	public List<Korisnik> findAll(){
		List<Korisnik> users = new ArrayList<Korisnik>();
		
		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery("for $doc in fn:collection('korisnici/') return $doc");
		
		EvalResultIterator response = invoker.eval();
		if (response.hasNext()) {
			for (EvalResult result : response) {
				try {
					Unmarshaller _unmarshaller = context.createUnmarshaller();
					Korisnik user = (Korisnik) _unmarshaller.unmarshal(new StringReader(result.getString()));
					users.add(user);
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("There is not result");	
		}
		
		return users;
	}

	@Override
	public Korisnik remove(String id) {
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		
		Korisnik user = findById(id);
		if (user != null)
			xmlManager.delete(id);
		
		// Return deleted user
		return user;
	}

	@Override
	public Korisnik persist(Korisnik user) {
		
		if (user.getId() == null) {
			user.setId(IDGenerator.generateID());
			try {
				user.setSifra(Hash.hash(user.getSifra()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			// Retrieve hashed password from database
			Korisnik u = findById(user.getId());
			user.setSifra(u.getSifra());
		}
		
		String fileName = user.getId();
		XMLDocumentManager docManager = client.newXMLDocumentManager();
		File file = new File(TOMEE_ROOT + DOCUMENT + fileName + ".xml");
		try {
			marshaller.marshal(user, file);
			InputStreamHandle handle = new InputStreamHandle(new FileInputStream(TOMEE_ROOT + DOCUMENT + fileName + ".xml"));

			docManager.write(fileName, metadata, handle);
			return user;
		} catch (JAXBException | FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	@Override
	public Korisnik findByAuthenticationData(AuthenticationData auth) {
		List<Korisnik> users = this.findAll();
		
		for(int i=0; i<users.size(); i++)
			if(users.get(i).getKorisnickoIme().equals(auth.getUsername()) && users.get(i).getSifra().equals(auth.getPassword()))
				return users.get(i);
		
		return null;
	}
}
