package rs.townhall.rest;

import java.util.UUID;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import rs.townhall.dao.UsersDao;
import rs.townhall.entity.user.Korisnik;
import rs.townhall.rest.model.AuthenticationData;

@Path("/auth")
public class Authentication {
	
	@EJB
	UsersDao usersDao;
	
	@Context 
	private ServletContext context;
	
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(AuthenticationData auth){
		
		try {
			Korisnik user = authenticate(auth);
			String token = generateToken();
			
			// Remember logged user
			this.context.setAttribute(token, user);
			
			return Response.ok(token).build();
		} catch(Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}	
	
	private String generateToken(){
		String token = UUID.randomUUID().toString();
		
		return token;
	}
	
	private Korisnik authenticate(AuthenticationData auth) throws Exception {
		if (auth.getPassword() == null)
			throw new Exception("Authentication error occurred");
		
		System.out.println("Username: " + auth.getUsername() + "\nPassword: " + auth.getPassword());
		System.out.println("Searching for user..");
		Korisnik user = usersDao.findByAuthenticationData(auth);
		if (user != null)
			return user;
		else
			throw new Exception("Authentication failed");
	}
}
