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

import rs.townhall.dao.UsersDao;
import rs.townhall.entity.user.Korisnik;

@Path("/users")
public class Users {

	@EJB
	private UsersDao usersDao;
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public List<Korisnik> getUsers(){
		List<Korisnik> users = this.usersDao.findAll();
		// Remove sensitive data - hashed passwords
		for(int i=0; i<users.size(); i++)
			users.get(i).setSifra("");
		
		return users;
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Korisnik getUserById(@PathParam("id") String id){
		Korisnik user = this.usersDao.findById(id);
		//Remove sensitive data - hashed password
		user.setSifra("");
		
		return user;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Korisnik addNewUser(Korisnik newUser){
		if (newUser.getId() != null)
			return newUser;
		
		Korisnik user = this.usersDao.persist(newUser);
		return user;
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	public Korisnik editUser(Korisnik user){
		if (user.getId() == null)
			return user;
		
		Korisnik editedUser = this.usersDao.persist(user);
		return editedUser;
	}
	
	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Korisnik deleteUser(@PathParam("id") String id){
		Korisnik user = this.usersDao.remove(id);
		return user;
	}
}
