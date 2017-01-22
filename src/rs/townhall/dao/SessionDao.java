package rs.townhall.dao;

import java.util.List;

import rs.townhall.entity.session.Sednica;

public interface SessionDao {
	public Sednica findById(String id);

	public List<Sednica> findAll();

	public Sednica remove(String id);

	public Sednica persist(Sednica session);
	
	public String setSessionState(String query);
	
	public String setTimetable(String query);
		
}
