package rs.townhall.rest;

import java.io.File;
import java.io.IOException;

import org.apache.fop.apps.FopFactory;
import org.xml.sax.SAXException;

public class TestTest {
	
	private static FopFactory fopFactory;
	
	public TestTest()
	{
		System.out.println("KONSTRUKTOR!");
		try {
			fopFactory = FopFactory.newInstance(new File("/home/kristina/Desktop/XMLProj/XML-project/src/fop.xconf"));
		} catch (SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
