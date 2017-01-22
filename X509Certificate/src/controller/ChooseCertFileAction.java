package controller;

import ib.security.CertificateGenerator;
import ib.security.IssuerData;
import ib.security.SubjectData;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPasswordField;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import view.EnterPasswordDialog;

public class ChooseCertFileAction extends AbstractAction{
	
	private PrivateKey privateKeyIssuer;
	private PublicKey publicKeyIssuer;
	private String passwordROOT= "kr";
	private X509Certificate cacert;
	private String path;
	private String reqName;
	private char[] pass;
	private EnterPasswordDialog parent;
	private String selectedPath;
	private char[] enteredPasswordCA;
	
	public static final String KEY_STORE_FILE = "./kljucevi/bezbednost.jks";
	public static final String CERT_REQUESTS = "./requests"; 

	
	public ChooseCertFileAction() {
		super();
		putValue(NAME, "Ok");
		putValue(SHORT_DESCRIPTION, "Confirm password. ");	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	
		reqName = super.getValue("imeFajla").toString();
		path = super.getValue("zahtev").toString();
		JComboBox cas = (JComboBox) super.getValue("odabraniCA");
		JPasswordField p = (JPasswordField) super.getValue("password");
		selectedPath = (String)super.getValue("selectedPath");
		pass = p.getPassword();
		enteredPasswordCA = ( char[] ) super.getValue("enteredPassword");
		JDialog d = (JDialog) super.getValue("parent");
		
		System.out.println(cas.getSelectedItem().toString());
		 
		String chosen = cas.getSelectedItem().toString();
		
		boolean isOk = setIssuerInfo(chosen);
		
		
		PEMParser pars;
		Object obj= null;
		
		try {
			pars = new PEMParser(new FileReader(path));
			obj = pars.readObject();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	
		
        PKCS10CertificationRequest csr = (PKCS10CertificationRequest)obj;
        SubjectPublicKeyInfo publicKey =  csr.getSubjectPublicKeyInfo();
        System.out.println(publicKey.getPublicKeyData().getString());
       
        //TODO: VRLO SUMNJIVO..
        
        RSAKeyParameters rsa = null;
        PublicKey subjectPublicKey = null;
		try {
			rsa = (RSAKeyParameters) PublicKeyFactory.createKey(publicKey);
		    RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(), rsa.getExponent());
		    KeyFactory kf = KeyFactory.getInstance("RSA");
		    subjectPublicKey = kf.generatePublic(rsaSpec);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (InvalidKeySpecException e1) {
			e1.printStackTrace();
		}
        
        X500Name name = csr.getSubject();
		
        AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
	    AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
	   
	    X500Name issuer = new X500Name(cacert.getSubjectX500Principal().getName());
        X500Name subject = csr.getSubject();

	    int validity = 365;
	    BigInteger serial = new BigInteger(32, new SecureRandom());
	    Date from = new Date();
	    Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));
	    
	    CertificateGenerator certGenerator = new CertificateGenerator();
	    IssuerData issuerData = new IssuerData(privateKeyIssuer, issuer);
	    
	    //TODO: DA LI MOZE publicKey.getPublic key??? -- NE MOZE
	    SubjectData subjectData = new SubjectData(subjectPublicKey, subject, "1000" ,from, to );
	    X509Certificate theCert  = certGenerator.generateCertificate(issuerData, subjectData);
	   
	    System.out.println("VALIDIRAJ..");
	    try {
			theCert.verify(publicKeyIssuer);
			//Cuva se uvek kod nas u ./certificates
			String saveAs = "./certificates/"+this.reqName.replace(".csr", "")+".crt";
			 FileOutputStream fos = new FileOutputStream(saveAs);
		     fos.write( theCert.getEncoded() );
			 fos.flush();
			 fos.close();
			 
			 // cuva se tamo gde je korisnik hteo..
			 if(getFileExtension(selectedPath).equals("jks"))
			 {
				 System.out.println("SACUVAJ SE OVDE: "+selectedPath);
				 KeyStore ks = KeyStore.getInstance("JKS", "SUN");
				 BufferedInputStream in = new BufferedInputStream(new FileInputStream(selectedPath));
				 ks.load(in, pass);
				 String alias = "MIIICA";
				 X500Name x500name = new JcaX509CertificateHolder(theCert).getSubject();
				 RDN cn = x500name.getRDNs(BCStyle.CN)[0];
				 alias = IETFUtils.valueToString(cn.getFirst().getValue());
				 System.out.println(theCert);
				 System.out.println("ALIAS "+theCert);
				 ks.setCertificateEntry(alias, theCert);
				 
				 BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(selectedPath));
				 ks.store(out, pass);
			 }
			 
			 d.dispose();
			    
		} catch (InvalidKeyException | CertificateException
				| NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		}
	    
	    System.out.println("GOTOVO....");
	    
	    
      
	}
	
	private boolean setIssuerInfo(String chosen)
	{
		try {
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, passwordROOT.toCharArray());
			System.out.println("Cita se Sertifikat i privatni kljuc...");
			System.out.println("Cita se za "+chosen);
			
			if(ks.isKeyEntry(chosen)) {
				cacert = (X509Certificate) ks.getCertificate(chosen);
				publicKeyIssuer = cacert.getPublicKey();
				System.out.println(cacert);
				privateKeyIssuer = (PrivateKey)ks.getKey(chosen,enteredPasswordCA);
				System.out.println("Privatni kljuc:");
				System.out.println(privateKeyIssuer);
				return true;
				
			}
			else
				System.out.println("Nema para kljuceva za Kristinu.. :( ");
		
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
		return false;
	}
	
	
	public String getFileExtension(String fi)
	{
		String extension = "";

		int i = fi.lastIndexOf('.');
		if (i > 0) {
		    extension = fi.substring(i+1);
		}
		
		return extension;
	}
	

}
