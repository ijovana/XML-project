package rs.townhall.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.print.attribute.standard.Media;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import rs.townhall.dao.SessionDao;
import rs.townhall.entity.amendment.Amandman;
import rs.townhall.entity.session.Sednica;

@Path("/sessions")
public class Sessions {
	@EJB
	private SessionDao sessionDao;
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Sednica> getSessions(){
		List<Sednica> sessions = this.sessionDao.findAll();
		return sessions;
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Sednica getSessionById(@PathParam("id") String id){
		Sednica session = this.sessionDao.findById(id);
		return session;
	}

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Sednica addNewSession(Sednica newSession){
		if (newSession.getId() != null)
			return newSession;
		
		Sednica session = this.sessionDao.persist(newSession);
		return session;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Sednica editSession(Sednica session){
		if (session.getId() == null)
			return session;
		
		Sednica editedSession = this.sessionDao.persist(session);
		return editedSession;
	}
	
	@DELETE
	@Path("/{id}")
	public Sednica deleteSession(@PathParam("id") String id){
		Sednica session = this.sessionDao.remove(id);
		return session;
	}	
	
	@PUT
	@Path("/{id}/{stanje}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String changeSessionState(@PathParam("id") String id, @PathParam("stanje") String stanje)
	{
		String res ="";
		String query = changeStateQuery(id, stanje);
		System.out.println("Query: "+query);
		res = sessionDao.setSessionState(query);
		return res;
	}
	
	public String changeStateQuery(String id, String state)
	{
		String query = "declare namespace sed = 'http://www.parlament.gov.rs/sednica';\nxdmp:node-replace(doc('"+id+"')//sed:stanje, <sed:stanje>"+state+"</sed:stanje>);\n";
		return query;	
	}
	
	
	@PUT
	@Path("/setTimetable/{id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String addTimetable(@PathParam("id") String id, String podaci)
	{
		String res = "";
		System.out.println("ID: "+id.toString());
		System.out.println("DATA: " +podaci.toString());
		String query = getTimetableQuery(id, podaci);
		System.out.println("QUERY: "+query);
		res = sessionDao.setTimetable(query);
		return res;
	}

	private String getTimetableQuery(String id, String podaci)
	{
		String query = "declare namespace sed = 'http://www.parlament.gov.rs/sednica';\n"
		+ "for $f in  <list>"+podaci+"</list>//sed:glasanje\n"
		+"return xdmp:node-insert-child(doc('"+id+"')/sed:sednica, $f );\n";
	
		return query;
	}

	
	@POST
	@Path("/insertResults/{id}")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public String insertResults(@PathParam("id") String id)
	{
		String res = "";
		String query = getInsertQuery(id);
	
		return res;
		
	}
	
	public String getInsertQuery(String id)
	{
		String queary = "declare namespace sed = 'http://www.parlament.gov.rs/sednica';\n"

		+	"<database>"
		+  "{"
		+   " let $sednice := collection('sednice/')/sed:sednica[@ID='ff4ae6a5-9094-4f65-a063-166b3c1fc9b3']"
		+    "for $sednica in $sednice"
		+    "(: where $sednica/sed:stanje = '_6k7S6v2ZoilRRp6' :)"
		 +   "for $glasanje in $sednica/sed:glasanje"
		 +   "for $pg in $glasanje/sed:predmet_glasanja"
		    
		 +   "where $glasanje/sed:broj_glasova_za > $glasanje/sed:broj_glasova_protiv + $glasanje/sed:broj_glasova_uzdrzani"
		 +   "xdmp:node-replace(doc($glasanje/sed:predmet_glasanja/@stanje), 'usvojen')"
		 + "return" 
		  +   "<res>"
		   +  "{ $pg }"
		 +   "</res>"   
		   
		 + "}"
		+ "</database>";
		
		return queary;
	}
}
