package rs.historicalarchive.dao;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import rs.townhall.entity.regulation.Propis;

public interface ArchiveDao {
	public void persist(Propis regulation) throws JAXBException, FileNotFoundException ;
}
