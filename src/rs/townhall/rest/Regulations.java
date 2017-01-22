package rs.townhall.rest;

import java.io.File;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import rs.townhall.dao.RegulationsDao;
import rs.townhall.entity.regulation.Propis;

@Path("/regulations")
public class Regulations {
	
	private static final String TOMEE_ROOT = System.getProperty("catalina.base");
	private static final String PDF = "/_data/pdf/";

	@EJB
	private RegulationsDao regulationsDao;
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Propis> getRegulations(){
		List<Propis> regulations = this.regulationsDao.findAll();
		return regulations;
	}
	
	@GET
	@Path("/titles")
	@Produces(MediaType.APPLICATION_XML)
	public List<Propis> getRegulationsAsTitles() {
		List<Propis> regulations = this.regulationsDao.findAll();
		return regulations;
	}
	
	@GET
	@Path("/search/{search}")
	@Produces(MediaType.APPLICATION_XML)
	public List<Propis> getRegulationsBySearch(@PathParam("search") String search) {
		List<Propis> regulations = this.regulationsDao.findBySearch(search);
		return regulations;
	}

	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Propis getRegulationById(@PathParam("id") String id){
		Propis regulation = this.regulationsDao.findById(id);
		return regulation;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Propis addNewRegulation(Propis newRegulation){
		if (newRegulation.getId() != null)
			return newRegulation;
		
		Propis regulation = this.regulationsDao.persist(newRegulation);
		return regulation;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Propis editRegulation(Propis regulation){
		if (regulation.getId() == null)
			return regulation;
		
		Propis editedRegulation = this.regulationsDao.persist(regulation);
		return editedRegulation;
	}
	
	@DELETE
	@Path("/{id}")
	public Propis deleteRegulation(@PathParam("id") String id){
		Propis regulation = this.regulationsDao.remove(id);
		return regulation;
	}
	
	@POST
	@Path("archive/{id}")
	public Response archiveRegulation(@PathParam("id") String id) {
		boolean success = regulationsDao.archive(id);
		if (success)
			return Response.status(Response.Status.OK).build();
		else 
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}
	
	@GET
	@Path("getSectionIds/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public String getSectionIds(@PathParam("id") String id)
	{
		String query = getSectionQuery(id);
		String response = regulationsDao.getSectionIds(id, query);
		return response;
	}
	
	public String getSectionQuery(String id)
	{
		String query = "declare namespace am = 'http://www.parlament.gov.rs/amandman';\n"
		+"declare namespace pr = 'http://www.parlament.gov.rs/propisi';\n"
		+"<database>\n"
		+ "{\n"
		+   "let $propisi := collection('propisi/')/pr:propis[@ID='"+id+"']\n"
		+    "for $propis in $propisi\n"
		+ 	 "let $odeljci := $propis//pr:odeljak\n"
		+    "for $odeljak in $odeljci\n"
		+      "return " 
		+      "<result>\n"
		+			"<id>\n"
		+        		"{ $odeljak/@ID }\n"
		+			"</id>\n"
		+			"<naziv>\n"	
		+				" { $odeljak/@naziv} \n"
		+			"</naziv>\n"
		+      "</result>\n"
		+  "}\n"
		+  "</database>\n";
		
		return query;
	}
	
	
	@GET
	@Path("getArticleIds/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public String getArticleIds(@PathParam("id") String id)
	{
		String query = getArticleQuery(id);
		String response = regulationsDao.getArticleIds(id, query);
		return response;
	}
	
	
	public String getArticleQuery(String id)
	{
		String query = "declare namespace am = 'http://www.parlament.gov.rs/amandman';\n"
		+ "declare namespace pr = 'http://www.parlament.gov.rs/propisi';\n"
		+ "<database>\n"
		+ "{\n"
		+   "let $odeljci := collection('propisi/')/pr:propis//pr:odeljak[@ID='"+id+"']\n"
		+    "for $odeljak in $odeljci\n"
		+    "let $clanovi := $odeljak//pr:clan\n"
		+    "for $clan in $clanovi\n"
		+      "return" 
		+      "<result>\n"
		+        "<id>\n"
		+        "{ $clan/@ID }\n"
		+        "</id>\n"
		+        "<naziv>\n"
		+			"{ $clan/@naziv }\n"
		+        "</naziv>\n"
		+      "</result>\n"
		+  "}\n"
		+	"</database>\n";
		
		return query;
	}
	
	@GET
	@Path("/getSuggestedRegulations")
	@Produces(MediaType.APPLICATION_XML)
	public String getSuggested()
	{	
		String response = "";
		String query = getSuggestedQuery();
		response = regulationsDao.getSuggestedRegulations(query);
		return response;
	}
	
	
	public String getSuggestedQuery()
	{
		String query = "declare namespace am = 'http://www.parlament.gov.rs/amandman';\n"
		+ "declare namespace pr = 'http://www.parlament.gov.rs/propisi';\n"
		+ "<database>\n"
		+  "{\n"
		+    "let $propisi := collection('propisi/')/pr:propis\n"
		+    "for $propis in $propisi\n"
		+    "where $propis/@stanje='predlozen'\n"
		+      "return" 
		+      "<result>\n"
		+        "<id>\n"
		+        "{ $propis/@ID }\n"
		+        "</id>\n"
		+        "<naziv>\n"
		+         "{ $propis/pr:naziv }\n"
		+        "</naziv>\n"
		+      "</result>\n"
		+  "}\n"
		+ "</database>\n";
		
		
		return query;
	};
	
	@GET
	@Path("pdf/{id}")
	@Produces("application/pdf")
	public Response getRegulationPDFById(@PathParam("id") String id) {
		System.out.println("Generating PDF for download..");
		this.regulationsDao.generatePdf(id);
		
		String FILE_NAME = id + ".pdf";
		File file = new File(TOMEE_ROOT + PDF + FILE_NAME);
		 
		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition", "attachment; filename=" + FILE_NAME);
		return response.build();
	}
}
