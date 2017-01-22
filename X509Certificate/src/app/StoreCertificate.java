package app;
import ib.security.CertificateGenerator;
import ib.security.IssuerData;
import ib.security.KeyStoreWriter;
import ib.security.SubjectData;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.security.auth.x500.X500Principal;

import model.ClientData;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;


public class StoreCertificate {
	
	public static final String KEY_STORE = "./kljucevi/bezbednost.jks";
	private char[] password = "kr".toCharArray();
	
	public StoreCertificate()
	{
		
	}
	
	public void storeCertificateInJKS(ClientData clientData) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException
	{
		  System.out.println("PISEMO U KEYSTORE...");
		  try{
			  
			CertificateGenerator gen = new CertificateGenerator();
			KeyPair keyPair = gen.generateKeyPair();
			SimpleDateFormat iso8601Formater = new SimpleDateFormat("yyyy-MM-dd");
			Date startDate = iso8601Formater.parse("2007-12-31");
			Date endDate = iso8601Formater.parse("2020-12-31");
			
			X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
			builder.addRDN(BCStyle.CN, clientData.getCommonName().toString());
			builder.addRDN(BCStyle.O, clientData.getOrganizationName().toString());
			builder.addRDN(BCStyle.OU, clientData.getOrganizationUnit().toString());
			builder.addRDN(BCStyle.C, clientData.getCountry().toString());
			builder.addRDN(BCStyle.E, clientData.getEmail().toString());
			builder.addRDN(BCStyle.L, clientData.getLocalityName().toString());
			builder.addRDN(BCStyle.ST, clientData.getStateName().toString());
			builder.addRDN(BCStyle.UID, "1");
			
			
			String sn="1";
			IssuerData issuerData = null;
			
			issuerData = setIssuerData();
			
			SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);
			X509Certificate cert = gen.generateCertificate(issuerData, subjectData);
			
			KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
			keyStoreWriter.loadKeyStore(KEY_STORE, password);
			keyStoreWriter.write(clientData.getCommonName().toLowerCase(), keyPair.getPrivate(), clientData.getPassword().toString().toCharArray(), cert);
			keyStoreWriter.saveKeyStore(KEY_STORE, password);
			
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE));
			ks.load(in, password);
			
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
	}
	
	
	private IssuerData setIssuerData() throws CertificateEncodingException
	{
		PrivateKey privKey = null;
		Certificate cert = null;
		try {
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE));
			ks.load(in, password);
			
			if(ks.isKeyEntry("skupstinans.com")) {
				cert = ks.getCertificate("skupstinans.com");
				privKey = (PrivateKey)ks.getKey("skupstinans.com", "skupstina".toCharArray());
				System.out.println(privKey);
			}
			else
				System.out.println("Nema para kljuceva za skupstinu...");
		
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		X500Name x500name = new JcaX509CertificateHolder((X509Certificate) cert).getSubject();
		RDN commname = x500name.getRDNs(BCStyle.CN)[0];
		String cn = IETFUtils.valueToString(commname.getFirst().getValue());
		RDN oN = x500name.getRDNs(BCStyle.O)[0];
		String o = IETFUtils.valueToString(oN.getFirst().getValue());
		RDN oU = x500name.getRDNs(BCStyle.OU)[0];
		String ou = IETFUtils.valueToString(oU.getFirst().getValue());
		RDN C = x500name.getRDNs(BCStyle.C)[0];
		String c = IETFUtils.valueToString(C.getFirst().getValue());
		RDN E = x500name.getRDNs(BCStyle.E)[0];
		String e = IETFUtils.valueToString(E.getFirst().getValue());
	
		
		X500NameBuilder buil = new X500NameBuilder();
		buil.addRDN(BCStyle.CN, cn);
		buil.addRDN(BCStyle.O, o);
		buil.addRDN(BCStyle.OU, ou );
		buil.addRDN(BCStyle.C, c);
		buil.addRDN(BCStyle.E, e);
		buil.addRDN(BCStyle.UID, "1");
		
		
		System.out.println("BUILDER : "+buil.toString());
		IssuerData issuerData = new IssuerData(privKey, buil.build());
		return issuerData;
	}
	

}
