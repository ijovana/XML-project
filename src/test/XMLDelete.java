package test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import util.Util;
import util.Util.ConnectionProperties;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.InputStreamHandle;

public class XMLDelete {
private static DatabaseClient client;
	
	public static void run(ConnectionProperties props) throws FileNotFoundException {
	
		// Initialize the database client
		if (props.database.equals("")) {
			System.out.println("[INFO] Using default database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.user, props.password, props.authType);
		} else {
			System.out.println("[INFO] Using \"" + props.database + "\" database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		}
		
		// Create a document manager to work with XML files.
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		
		String testDocId = "Mirjana";
		
		
		// Document deletion
		System.out.println("[INFO] Removing \"" + testDocId + "\" from \"" + props.database + "\" database.");
		xmlManager.delete(testDocId);
		
	// Release the client
		client.release();
		
		System.out.println("[INFO] End.");
	}

	public static void main(String[] args) throws IOException {
		run(Util.loadProperties());
	}

}
