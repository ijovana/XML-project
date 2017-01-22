package rs.townhall.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/api")
public class TownHall {

	@GET
	@Path("/sayHi")
	public String sayHi(){
		return "Hello";
	}
	
}
