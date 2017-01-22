package ib.util;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

public class CrlUtil {
	public static void saveCRLfile(String path, X509CRL crl) {
		System.out.println(crl);
		
		File crlFile = new File(path);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(crlFile);
			fos.write(crl.getEncoded());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CRLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static X509CRL openFromFile(String path) {
		try {
			FileInputStream fis = new FileInputStream(path);			
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509CRL crl = (X509CRL) cf.generateCRL(fis);
			System.out.println(crl);
			
			return crl;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (CRLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static PrivateKey readPrivateKey(String path) {
		File f = new File(path);
	    FileInputStream fis;
		try {
			fis = new FileInputStream(f);
			 DataInputStream dis = new DataInputStream(fis);
		    byte[] keyBytes = new byte[(int)f.length()];
		    dis.readFully(keyBytes);
		    dis.close();

		    
		  
		    PKCS8EncodedKeySpec spec =
		      new PKCS8EncodedKeySpec(keyBytes);
		    KeyFactory kf = KeyFactory.getInstance("RSA");
		    PrivateKey pk=kf.generatePrivate(spec);
		    return pk;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
