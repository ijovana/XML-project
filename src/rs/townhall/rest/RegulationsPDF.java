package rs.townhall.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@Path("/regulationsPDF")
public class RegulationsPDF {
	
	public static final String  PATH_KRISTINA = "/home/kristina/Desktop/XMLProj/XML-project/data/gen/";
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRegulationPDF(@PathParam("id") int id)
	{
		//String str = "{'user':'kristina', 'id: "+id+"'}";
		System.out.println("HEREEEEEEE");
		String str = "{'nesto': 'nesto'}";
		
		return str;
	}
	
	@GET
	@Path("/{id}/pdf")
	@Produces("application/pdf")
	public Response getFile(@PathParam("id") int id)
	{
		File file = new File(PATH_KRISTINA+"bookstore_result.pdf");

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition",
				"attachment; filename=new-android-book.pdf");
		return response.build();
	}
	
	
	@GET
	@Path("/{id}.pdf")
	@Produces(MediaType.APPLICATION_JSON)
	public String getSomething(@PathParam("id") int id)
	{
		String s = "{'woooho': 'jeeej'}";
		return s;
	}
	
	
	@GET
	@Path("/nesto")
	@Produces(MediaType.APPLICATION_JSON)
	public Response showFile(@PathParam("id") int id)
	{
		File file = new File(PATH_KRISTINA+"bookstore_result.pdf");

		ResponseBuilder response = Response.ok((Object) file);
		response.header("Content-Disposition",
				"filename=test.pdf");
		return response.build();
	}
	
	@GET
	@Path("/n")
	@Produces(MediaType.APPLICATION_JSON)
	public Response tryShowFile(@PathParam("id") int id) throws FileNotFoundException
	{
		 File file = new File(PATH_KRISTINA+"bookstore_result.pdf");
		 FileInputStream fileInputStream = new FileInputStream(file);
		 javax.ws.rs.core.Response.ResponseBuilder responseBuilder = javax.ws.rs.core.Response.ok((Object) fileInputStream);
		 responseBuilder.type("application/pdf");
		 responseBuilder.header("Content-Disposition", "filename=test.pdf");
		 return responseBuilder.build();
	}
	
}
