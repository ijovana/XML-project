package rs.historicalarchive.rest;

import java.io.StringReader;
import java.security.PrivateKey;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.w3c.dom.Document;

import rs.historicalarchive.dao.ArchiveDao;
import rs.townhall.entity.regulation.Propis;
import rs.townhall.security.DecryptKEK;
import rs.townhall.security.LoadDocument;

@Path("/archive")
public class HistoricalArchive {
	
	private static final String TOMEE_ROOT = System.getProperty("catalina.base");
	private static final String PRIVATE_KEY = "/_data/security/arhiv.key";
	private static JAXBContext context;
	private static Unmarshaller unmarshaller;

	static {
		try {
			context = JAXBContext.newInstance(Propis.class);
			unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			System.out.println(e.getMessage());
		}
	}

	@EJB
	ArchiveDao archiveDao;

	@GET
	@Path("/ping")
	public Response ping() {
		return Response.status(Response.Status.ACCEPTED).entity("Historical archive: Status OK").build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Path("/regulation")
	public Response addRegulation(String xmlRegulation) {
		
		// Decrypt regulation
		Propis regulation = decrypt(xmlRegulation);
		if (regulation == null)
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();

		try {
			archiveDao.persist(regulation);
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Status: 500
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	private Propis decrypt(String xmlRegulation) {
	
		Document doc = LoadDocument.convertStringToDocument(xmlRegulation);
		PrivateKey pk = DecryptKEK.readPrivateKey(TOMEE_ROOT + PRIVATE_KEY);
		Document decriptedDoc = DecryptKEK.decrypt(doc, pk);
		xmlRegulation = LoadDocument.documentToString(decriptedDoc);
		
		StringReader reader = new StringReader(xmlRegulation);
		Propis regulation;
		try {
			regulation = (Propis) unmarshaller.unmarshal(reader);
			return regulation;
		} catch (JAXBException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
