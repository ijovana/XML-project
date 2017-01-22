package rs.townhall.dao;

import java.util.List;

import rs.townhall.entity.amendment.Amandman;

public interface AmendmentsDao {

	public Amandman findById(String id);

	public List<Amandman> findAll();

	public Amandman remove(String id);

	public Amandman persist(Amandman amendment);
	
	public List<Amandman> findBySearch(String content);
	
	public String getSuggestedAmendments(String query);

	public List<Amandman> findByAdvancedSearch(String search);
}
