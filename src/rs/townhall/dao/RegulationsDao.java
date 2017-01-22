package rs.townhall.dao;

import java.util.List;
import rs.townhall.entity.regulation.Propis;

public interface RegulationsDao {

	public Propis findById(String id);

	public List<Propis> findAll();

	public Propis remove(String id);

	public Propis persist(Propis regulation);
	
	public boolean archive(String id);
	
	public List<Propis> findBySearch(String search);
	 
	public String getSectionIds(String id, String query);
	
	public String getArticleIds(String id, String query);
	
	public String getSuggestedRegulations(String query);
	
	public void generatePdf(String id);

	public List<Propis> findByAdvancedSearch(String search);
}
