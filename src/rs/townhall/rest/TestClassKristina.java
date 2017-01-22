package rs.townhall.rest;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import util.Util;
import util.Util.ConnectionProperties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;

public class TestClassKristina {
	
	public static final String  PATH_KRISTINA = "/home/kristina/Desktop/XMLProj/XML-project/data/xsl-fo/";
	private static DatabaseClient client;
	private static TransformerFactory transformerFactory;
	
	
	static {
		transformerFactory = TransformerFactory.newInstance();
	}
	
	public static void getXML(String docId) throws IOException
	{
		ConnectionProperties props = Util.loadProperties();
		System.out.println("START.");	
		System.out.println("[INFO] " + TestClassKristina.class.getSimpleName());
		if (props.database.equals("")) {
			System.out.println("[INFO] Using default database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.user, props.password, props.authType);
		} else {
			System.out.println("[INFO] Using \"" + props.database + "\" database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		}
		
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		DOMHandle content = new DOMHandle();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		//String docId = "propis.xml";
		System.out.println("[INFO] Retrieving \"" + docId + "\" from "
				+ (props.database.equals("") ? "default" : props.database)
				+ " database.");
		
		xmlManager.read(docId, metadata, content);
		Document doc = content.get();
		System.out.println("[INFO] Assigned collections: " + metadata.getCollections());
		System.out.println("[INFO] Retrieved content:");
		OutputStream out = new BufferedOutputStream(new FileOutputStream(PATH_KRISTINA+docId+".xml"));
		
		// writing file to /data/gen/docId
		transform(doc, out);
		
		client.release();
		System.out.println("[INFO] End.");
		
	}
	
	private static void transform(Node node, OutputStream out) {
		try {
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(node);
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
	
	//public static void main(String[] args) throws IOException
	//{
	//	TestClassKristina.getXML("KRISTINAPROBA");
	//}

}
