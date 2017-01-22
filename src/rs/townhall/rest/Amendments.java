package rs.townhall.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import rs.townhall.dao.AmendmentsDao;
import rs.townhall.entity.amendment.Amandman;

@Path("/amendments")
public class Amendments {

	@EJB
	private AmendmentsDao amendmentsDao;
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Amandman> getAmendments(){
		List<Amandman> amendments = this.amendmentsDao.findAll();
		return amendments;
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Amandman getAmendmentById(@PathParam("id") String id){
		Amandman amendment = this.amendmentsDao.findById(id);
		return amendment;
	}
	
	@GET
	@Path("/search/{search}")
	@Produces(MediaType.APPLICATION_XML)
	public List<Amandman> getAmendmentBySearch(@PathParam("search") String search){
		List<Amandman> amendments = this.amendmentsDao.findBySearch(search);
		return amendments;
	}

	@GET
	@Path("/advancedSearch/{search}")
	@Produces(MediaType.APPLICATION_XML)
	public List<Amandman> getAmendmentByAdvancedSearch(@PathParam("search") String search){
		List<Amandman> amendments = this.amendmentsDao.findByAdvancedSearch(search);
		return amendments;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Amandman addNewAmendment(Amandman newAmendment){
		if (newAmendment.getId() != null)
			return newAmendment;
		
		Amandman amendment = this.amendmentsDao.persist(newAmendment);
		return amendment;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Amandman editAmendment(Amandman amendment){
		if (amendment.getId() == null)
			return amendment;
		
		Amandman editedAmendment = this.amendmentsDao.persist(amendment);
		return editedAmendment;
	}
	
	@DELETE
	@Path("/{id}")
	public Amandman deleteAmendments(@PathParam("id") String id){
		Amandman amendment = this.amendmentsDao.remove(id);
		return amendment;
	}		
	
	@GET
	@Path("/getSuggestedAmendments")
	@Produces(MediaType.APPLICATION_XML)
	public String getSuggested()
	{	
		String response = "";
		String query = getSuggestedQuery();
		response = amendmentsDao.getSuggestedAmendments(query);
		return response;
	}
	
	public String getSuggestedQuery()
	{
		String query = "declare namespace am = 'http://www.parlament.gov.rs/amandman';\n"
		+	"declare namespace pr = 'http://www.parlament.gov.rs/propisi';\n"
		+	"<database>\n"
		+  "{\n"
		+    "let $amandmani := collection('amandmani/')/am:amandman\n"
		+    "for $amandman in $amandmani\n"
		+    "where $amandman/@stanje='predlozen'\n"
		+      "return" 
		+      "<result>\n"
		+       "<id>"
		+       "{ $amandman/@ID }\n"
		+        "</id>\n"
		+        "<naziv>\n"
		+         "{ $amandman/@naziv }\n"
		+        "</naziv>\n"
		+      "</result>\n"
		+  "}\n"
		+	"</database>\n";
		
		return query;
	}
	
}
