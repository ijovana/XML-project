package rs.townhall.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/townhall")
public class TownHall {

	@GET
	@Path("/sayHi")
	public String sayHi(){
		return "Hello";
	}
	
}
