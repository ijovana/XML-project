package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import rs.townhall.entity.amendment.Amandman;
import rs.townhall.entity.regulation.Propis;
import util.MyValidationEventHandler;
import util.NSPrefixMapper;
import util.Util;
import util.Util.ConnectionProperties;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;

public class XMLWriter {

	private static ObjectMapper m=new ObjectMapper();
	private static JsonFactory jf=new JsonFactory();
	private static DatabaseClient client;
	
	public static void run(ConnectionProperties props) throws FileNotFoundException, JAXBException, IOException, URISyntaxException {

		//System.out.println("[INFO] " + HelloTextWriterExample.class.getSimpleName());
		
		// Initialize the database client
	if (props.database.equals("")) {
			System.out.println("[INFO] Using default database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.user, props.password, props.authType);
		} else {
			System.out.println("[INFO] Using \"" + props.database + "\" database.");
			client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		}
				
		
	/*	Korisnik k=new Korisnik();
		
		k.setIme("Mirjana");
		k.setPrezime("Curcin");
		k.setKorisnickoIme("Mima");
		k.setSifra("mima");
		k.setUloga("admin");
		
		try {
		
			// Defini�e se JAXB kontekst (putanja do paketa sa JAXB bean-ovima)
			JAXBContext context = JAXBContext.newInstance(Korisnik.class);
			
			// Unmarshaller je objekat zadu�en za konverziju iz XML-a u objektni model
			Marshaller marshaller = context.createMarshaller();
			
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.parlament.gov.rs/korisnici file:/data/korisnik.xsd");
			// Izmena nad objektnim modelom dodavanjem novog odseka
			File file=new File("./data/korisnik2.xml");
			
			marshaller.marshal(k, System.out);
			
			marshaller.marshal(k, file);
			   // Writing to console
			   
			  } catch (JAXBException e) {
			   // some exception occured
			   e.printStackTrace();
			  }
		*/
	String json ="{\"redni_broj\":\"vXPO38rpipqhipkHt99d3\",\"datum\":\"2017-10-20\",\"naziv\":\"AMANDMAN\",\"naziv_predloga\":\"u7ME3O4d7A2W\",\"odredba_predloga\":{\"clan\":{\"broj\":\"½.\",\"naziv\":\"Do92kfpL5IpPOK\",\"naslov\":\"njf0n6QVceibht\",\"sadrzaj\":{\"link\":{\"id\":\"Jk4h_ldbh9LH_ElsPCuyykp-eW\"},\"stav\":[{\"id\":5,\"tacka\":[{\"naziv\":\"4062)ra\",\"podtacka\":[{\"naziv\":\"(832)i\",\"alineje\":[\"- nqc\",\"- jt\"]},{\"naziv\":\"(7)nvk\",\"alineje\":[\"-\",\"-\"]}]},{\"naziv\":\"1)\",\"podtacka\":[{\"naziv\":\"(3)tb\",\"alineje\":[\"- tf\",\"-\"]},{\"naziv\":\"(5)\",\"alineje\":[\"-\",\"-\"]}]}]},{\"id\":3,\"tacka\":[{\"naziv\":\"9)zm\",\"podtacka\":[{\"naziv\":\"(2)u\",\"alineje\":[\"- t\",\"- nf\"]},{\"naziv\":\"(4)\",\"alineje\":[\"-\",\"-\"]}]},{\"naziv\":\"001)\",\"podtacka\":[{\"naziv\":\"(2)\",\"alineje\":[\"-\",\"- ssz\"]},{\"naziv\":\"(6402)eh\",\"alineje\":[\"-\",\"-\"]}]}]}]}}},\"predlozeno_resenje\":{\"brisanje\":1},\"obrazlozenje\":\"mJsTB7emeho5i_14AZ\",\"podnosilac\":[{\"ime\":\"VjRP8sO\",\"prezime\":\"YZuOLpL.BqMO\"},{\"ime\":\"ILR69w4QF\",\"prezime\":\"fKC\"}],\"sadrzaj\":\"\"}";
	
	Amandman a=new Amandman();
	File file = null;
	XMLDocumentManager docManager = client.newXMLDocumentManager();
	Unmarshaller unmarshaller;
	
	try {
		a=(Amandman) m.readValue(json, Amandman.class);
		
		
		JAXBContext context = JAXBContext.newInstance(Amandman.class);
		Marshaller marshaller = context.createMarshaller();
		InputStreamHandle handle = new InputStreamHandle(new FileInputStream("C:/Users/Mima/Documents/GitHub/XML-project/data/amandman/"+a.getNaziv()+".xml"));
		
		
		marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NSPrefixMapper());
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.parlament.gov.rs/amandman file:/C:/Users/Mima/Documents/GitHub/XML-project/data/amandman.xsd");
		file=new File("C:/Users/Mima/Documents/GitHub/XML-project/data/"+a.getNaziv()+".xml");
		
		marshaller.marshal(a, file);
		
		//context = JAXBContext.newInstance(Amandman.class);
		//JAXBHandle handle=new JAXBHandle(context);
		
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		metadata.getCollections().add("amandmani/");
		unmarshaller = context.createUnmarshaller();
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;
		schema = schemaFactory.newSchema(new File("C:/Users/Mima/Documents/GitHub/XML-project/data/amandman.xsd"));
		unmarshaller.setSchema(schema);
		unmarshaller.setEventHandler(new MyValidationEventHandler());
        String docId = a.getNaziv();

		//Amandman am = (Amandman) unmarshaller.unmarshal(file);	// deserialize the POJO

		//handle.set(am);// provide a handle for the POJO
		
		docManager.write(docId,metadata, handle);
	        
	} catch (JsonParseException e) {
		e.printStackTrace();
	} catch (JsonMappingException e) {
		
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	} catch (SAXException e) {
		e.printStackTrace();
	} catch (JAXBException e) {
		e.printStackTrace();
	}
	

	}
	public static void main(String[] args) throws IOException, JAXBException, URISyntaxException {
		XMLWriter.run(Util.loadProperties());
		
	}
	}
	