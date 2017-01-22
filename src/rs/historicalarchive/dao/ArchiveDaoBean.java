package rs.historicalarchive.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

import rs.townhall.entity.regulation.Propis;
import util.MyValidationEventHandler;
import util.NSPrefixMapper;
import util.Util;
import util.Util.ConnectionProperties;

@Stateless
@Local(ArchiveDao.class)
public class ArchiveDaoBean implements ArchiveDao {

	private static DatabaseClient client;
	private static JAXBContext context;
	private static Unmarshaller unmarshaller;
	private static Marshaller marshaller;
	private static Schema schema;
	private static DocumentMetadataHandle metadata;
	private static final String TOMEE_ROOT = System.getProperty("catalina.base");
	private static final String DOCUMENT = "/_data/documents/";
	private static final String SCHEMA = "/_data/schemas/propis.xsd";
	private static final String HISTORICAL_ARCHIVE_PREFIX = "acrhieve_";

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
		metadata.getCollections().add("arhiva/");
		// Setting marshaller/unmarshaller
		try {
			context = JAXBContext.newInstance(Propis.class);
			unmarshaller = context.createUnmarshaller();
			unmarshaller.setSchema(schema);
			marshaller = context.createMarshaller();
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION,
					"http://www.parlament.gov.rs/propisi file:/" + TOMEE_ROOT + SCHEMA);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void persist(Propis regulation) throws JAXBException, FileNotFoundException {

		String fileName = HISTORICAL_ARCHIVE_PREFIX + regulation.getId();
		XMLDocumentManager docManager = client.newXMLDocumentManager();
		File file = new File(TOMEE_ROOT + DOCUMENT + fileName + ".xml");
		marshaller.marshal(regulation, file);
		InputStreamHandle handle = new InputStreamHandle(new FileInputStream(TOMEE_ROOT + DOCUMENT + fileName + ".xml"));
		unmarshaller.setEventHandler(new MyValidationEventHandler());

		docManager.write(fileName, metadata, handle);
	}
}
