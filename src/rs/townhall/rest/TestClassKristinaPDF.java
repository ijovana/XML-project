package rs.townhall.rest;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.apache.xalan.processor.TransformerFactoryImpl;
import org.xml.sax.SAXException;

public class TestClassKristinaPDF {

	
	public static final String  PATH_KRISTINA = "D:/Development/Eclipse/workspace/XML-project/data/gen/";

	private FopFactory fopFactory;
	
	private TransformerFactory transformerFactory;
	
	public TestClassKristinaPDF() throws SAXException, IOException {
		
		System.out.println("KONSTRUKTOR ");
		// Initialize FOP factory object
		fopFactory = FopFactory.newInstance(new File("D:/Development/Eclipse/workspace/XML-project/src/fop.xconf"));
		System.out.println("FOPFACTORY created");
		// Setup the XSLT transformer factory
		transformerFactory = new TransformerFactoryImpl();
	}
	
	
	public  void createPDF(String name) throws Exception {
		
		System.out.println("[INFO] " + TestClassKristinaPDF.class.getSimpleName());
		
		// Point to the XSL-FO file
		File xsltFile = new File("D:/Development/Eclipse/workspace/XML-project/data/xsl-fo/propis.xsl");

		// Create transformation source
		StreamSource transformSource = new StreamSource(xsltFile);
		
		// Initialize the transformation subject
		StreamSource source = new StreamSource(new File("D:/Development/Eclipse/workspace/XML-project/data/xsl-fo/"+name+""));

		// Initialize user agent needed for the transformation
		FOUserAgent userAgent = fopFactory.newFOUserAgent();
		
		// Create the output stream to store the results
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		// Initialize the XSL-FO transformer object
		Transformer xslFoTransformer = transformerFactory.newTransformer(transformSource);
		
		// Construct FOP instance with desired output format
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, userAgent, outStream);

		// Resulting SAX events 
		Result res = new SAXResult(fop.getDefaultHandler());

		// Start XSLT transformation and FOP processing
		xslFoTransformer.transform(source, res);

		// Generate PDF file
		File pdfFile = new File("D:/Development/Eclipse/workspace/XML-project/data/gen/"+name+".pdf");
		OutputStream out = new BufferedOutputStream(new FileOutputStream(pdfFile));
		out.write(outStream.toByteArray());

		System.out.println("[INFO] File \"" + pdfFile.getCanonicalPath() + "\" generated successfully.");
		out.close();
		
		System.out.println("[INFO] End.");

	}
	
	public static void main(String[] args) throws Exception {
		new TestClassKristinaPDF().createPDF("propis2.xml");
		 String name = "S";
		 TestClassKristina.getXML(name);
		 new TestClassKristinaPDF().createPDF(name);
	}
	
}

