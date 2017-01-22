package ib.security;


import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class CertificateGenerator {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public X509Certificate generateCertificate(IssuerData issuerData, SubjectData subjectData) {
		 try {

			 JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");
			 builder = builder.setProvider("BC");
			 ContentSigner contentSigner = builder.build(issuerData.getPrivateKey());
			 X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
					 															new BigInteger(subjectData.getSerialNumber()),
					 															subjectData.getStartDate(),
					 															subjectData.getEndDate(),
					 															subjectData.getX500name(),
					 															subjectData.getPublicKey());

			 X509CertificateHolder certHolder = certGen.build(contentSigner);
			 JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
			 certConverter = certConverter.setProvider("BC");
			 return certConverter.getCertificate(certHolder);
			 
		 } catch (CertificateEncodingException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (OperatorCreationException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public KeyPair generateKeyPair() {
		try {
			
			KeyPairGenerator   keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(1024);
			KeyPair pair = keyGen.generateKeyPair();	
			return pair;
			
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void testIt() throws IOException {
		//kreira se self signed sertifikat
		try {
			
			KeyPair keyPair = generateKeyPair();
			SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = iso8601Formater.parse("2007-12-31");
			Date endDate = iso8601Formater.parse("2017-12-31");
			
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
		    builder.addRDN(BCStyle.CN, "Goran Sladic");
		    builder.addRDN(BCStyle.SURNAME, "Sladic");
		    builder.addRDN(BCStyle.GIVENNAME, "Goran");
		    builder.addRDN(BCStyle.O, "UNS-FTN");
		    builder.addRDN(BCStyle.OU, "Katedra za informatiku");
		    builder.addRDN(BCStyle.C, "RS");
		    builder.addRDN(BCStyle.E, "sladicg@uns.ac.rs");
		    //UID (USER ID) je ID korisnika
		    builder.addRDN(BCStyle.UID, "123445");
			
			
			//Serijski broj sertifikata
			String sn="1";
			//kreiraju se podaci za issuer-a
			IssuerData issuerData = new IssuerData(keyPair.getPrivate(), builder.build());
			//kreiraju se podaci za vlasnika
			SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);
			
			//generise se sertifikat
			X509Certificate cert = generateCertificate(issuerData, subjectData);
			
			System.out.println("ISSUER: " + cert.getIssuerX500Principal().getName());
			System.out.println("SUBJECT: " + cert.getSubjectX500Principal().getName());
			System.out.println("Sertifikat:");
			System.out.println("-------------------------------------------------------");
			System.out.println(cert);
			System.out.println("-------------------------------------------------------");
			//ako validacija nije uspesna desice se exception
			
			//ovde bi trebalo da prodje
			cert.verify(keyPair.getPublic());
			System.out.println("VALIDACIJA USPESNA....");
			
			//ovde bi trebalo da se desi exception, jer validaciju vrsimo drugim kljucem
			//KeyPair anotherPair = generateKeyPair();
			//cert.verify(anotherPair.getPublic());
			
			FileOutputStream fos = new FileOutputStream("./data/kristina.cer");
			fos.write( cert.getEncoded() );
			fos.flush();
			fos.close();

			
			
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}
	}
	
	//public static void main(String[] args) throws IOException {
	//	CertificateGenerator gen = new CertificateGenerator();
	//	gen.testIt();
	//}

}
