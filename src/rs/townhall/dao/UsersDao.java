package rs.townhall.dao;

import java.util.List;
import rs.townhall.entity.user.Korisnik;
import rs.townhall.rest.model.AuthenticationData;

public interface UsersDao {

	public Korisnik findById(String id);

	public List<Korisnik> findAll();

	public Korisnik remove(String id);

	public Korisnik persist(Korisnik user);
	
	public Korisnik findByAuthenticationData(AuthenticationData auth);
}
