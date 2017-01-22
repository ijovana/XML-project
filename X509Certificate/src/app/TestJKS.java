package app;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.asn1.x509.X509Extension;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcRSAContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.util.encoders.Base64;

import ib.security.CertificateGenerator;
import ib.security.IssuerData;
import ib.security.KeyStoreWriter;
import ib.security.SubjectData;



public class TestJKS {

	public static final String KEY_STORE_FILE = "./kljucevi/bezbednost.jks";
	public static final String CERT_REQUESTS = "./requests/zahtevPrvi.csr";
	private char[] password = "kr".toCharArray();
	private char[] keyPass  = "k".toCharArray();
	

	
	public static void main(String[] args) throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeySpecException, UnrecoverableKeyException, OperatorCreationException, CMSException, InvalidKeyException, SignatureException {
		TestJKS test = new TestJKS();
		test.signCSR();
		//test.testIt();	
		//test.testWrite();
	}

	public TestJKS()
	{
	}
	
	
	public void signCSR() throws IOException, CertificateException, InvalidKeySpecException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, UnrecoverableKeyException, OperatorCreationException, CMSException, InvalidKeyException, SignatureException
	{
		System.out.println("CITANJE CSR FAJLA");
		String content = readFile(CERT_REQUESTS, StandardCharsets.UTF_8);
		System.out.println("ZAHTEV BYTES: ");
		System.out.println(content);
		
		
		PEMParser pars = new PEMParser(new FileReader(CERT_REQUESTS));
		Object obj = pars.readObject();
		
        PKCS10CertificationRequest csr = (PKCS10CertificationRequest)obj;
        SubjectPublicKeyInfo publicKey =  csr.getSubjectPublicKeyInfo();
        System.out.println(publicKey.getPublicKeyData().getString());
       
        //TODO: VRLO SUMNJIVO..
        RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory.createKey(publicKey);
        RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(), rsa.getExponent());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey subjectPublicKey = kf.generatePublic(rsaSpec);
        
        X500Name name = csr.getSubject();
        RDN cn = name.getRDNs(BCStyle.CN)[0];
        String ime = IETFUtils.valueToString(cn.getFirst().getValue());
        System.out.println("COMMON NAME: "+ime);
        
        
        // SIGN CERTIFICATE
        
        KeyStore keystore = KeyStore.getInstance("JKS", "SUN");
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
		keystore.load(in, password);
		String alias = "skupstinans.com";
		PrivateKey cakey = (PrivateKey)keystore.getKey(alias, "skupstina".toCharArray());
		
		X509Certificate cacert = (X509Certificate)keystore.getCertificate(alias);
		PublicKey caPublicKey = cacert.getPublicKey();
		
		AlgorithmIdentifier sigAlgId = new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA");
	    AlgorithmIdentifier digAlgId = new DefaultDigestAlgorithmIdentifierFinder().find(sigAlgId);
	   
	    X500Name issuer = new X500Name(cacert.getSubjectX500Principal().getName());
        X500Name subject = csr.getSubject();

	    int validity = 365;
	    BigInteger serial = new BigInteger(32, new SecureRandom());
	    Date from = new Date();
	    Date to = new Date(System.currentTimeMillis() + (validity * 86400000L));
	    
	    CertificateGenerator certGenerator = new CertificateGenerator();
	    IssuerData issuerData = new IssuerData(cakey, issuer);
	    
	    //TODO: DA LI MOZE publicKey.getPublic key??? -- NE MOZE
	    SubjectData subjectData = new SubjectData(subjectPublicKey, subject, "1000" ,from, to );
	    X509Certificate theCert  = certGenerator.generateCertificate(issuerData, subjectData);
	   
	    System.out.println("VALIDIRAJ..");
	    theCert.verify(caPublicKey);
		System.out.println("VALIDACIJA USPESNA....");
	    
	    FileOutputStream fos = new FileOutputStream("./certificates/sertifikatPrvi.crt");
		fos.write( theCert.getEncoded() );
		fos.flush();
		fos.close();
	    
	    System.out.println("GOTOVO....");
	}
	
	static String readFile(String path, Charset encoding) throws IOException 
	{
			  byte[] encoded = Files.readAllBytes(Paths.get(path));
			  return new String(encoded, encoding);
	}
	
	public void testIt()
	{
		System.out.println("CITAMO KEYSTORE BEZB:");
		readKeyStore();
	}
	
	public void testWrite() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException, CertificateException, IOException
	{ 
		System.out.println("PISEMO U KEYSTORE: ");
	  try{
		  
		CertificateGenerator gen = new CertificateGenerator();
		KeyPair keyPair = gen.generateKeyPair();
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
	    builder.addRDN(BCStyle.UID, "123445");
		String sn="1";
		IssuerData issuerData = new IssuerData(keyPair.getPrivate(), builder.build());
		SubjectData subjectData = new SubjectData(keyPair.getPublic(), builder.build(), sn, startDate, endDate);
		X509Certificate cert = gen.generateCertificate(issuerData, subjectData);
		
		KeyStoreWriter keyStoreWriter = new KeyStoreWriter();
		keyStoreWriter.loadKeyStore("./kljucevi/bezbednost.jks", "kr".toCharArray());
		keyStoreWriter.write("test", keyPair.getPrivate(), "test10".toCharArray(), cert);
		keyStoreWriter.saveKeyStore("./kljucevi/bezbednost.jks", "kr".toCharArray());
		
		
		KeyStore ks = KeyStore.getInstance("JKS", "SUN");
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
		ks.load(in, password);
		
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
	}
	
	public void readKeyStore()
	{		
		try {
			KeyStore ks = KeyStore.getInstance("JKS", "SUN");
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
			ks.load(in, password);
			System.out.println("Cita se Sertifikat i privatni kljuc...");
			
			if(ks.isKeyEntry("kristina")) {
				System.out.println("Sertifikat:");
				Certificate cert = ks.getCertificate("kristina");
				System.out.println(cert);
				PrivateKey privKey = (PrivateKey)ks.getKey("kristina", keyPass);
				System.out.println("Privatni kljuc:");
				System.out.println(privKey);
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
	}	
}
