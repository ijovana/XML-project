package test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;

public class TestKlasa {
	
	public static final String KEY_STORE_FILE = "./kljucevi/odbornici.jks";
	private char[] password = "kr".toCharArray();
	
	public void testIt() throws Exception
	{
		KeyStore keystore = KeyStore.getInstance("JKS", "SUN");
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(KEY_STORE_FILE));
		keystore.load(in, password);
		
		String alias = "mima";
		Boolean con = keystore.containsAlias(alias);
		System.out.println("KEYSTORE ODBORNICI contains alias mima "+con);
		
		X509Certificate cert = (X509Certificate)keystore.getCertificate(alias);
		PublicKey publicKey = cert.getPublicKey();
		System.out.println("Certificate "+ cert);
		System.out.println("Mima's public key: "+publicKey);
		
		// PROBLEM JE BIO FORMAT KLJUCA. (komanda za prabacivanje kljuca u pkcs format je 
		//openssl pkcs8 -topk8 -inform PEM -outform DER -in mima.key  -nocrypt > mimapkcs.key)
		
		PrivateKey privateKey = getPrivateKey("./privateKeys/mimapkcs.key");
		System.out.println(privateKey);
		
	}
	
	
	
	public PrivateKey getPrivateKey(String filename) throws Exception {

	    File f = new File(filename);
	    FileInputStream fis = new FileInputStream(f);
	    DataInputStream dis = new DataInputStream(fis);
	    byte[] keyBytes = new byte[(int) f.length()];
	    dis.readFully(keyBytes);
	    dis.close();
	    System.out.println(keyBytes);
	    KeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	    KeyFactory kf = KeyFactory.getInstance("RSA");
	    return kf.generatePrivate(spec);
	}
	
	public static void main(String[] args) throws Exception
	{
		new TestKlasa().testIt();
	}

	
	// MOZDA NEKAD ZATREBA...
	/*public void key() throws IOException
	{
		String keyPath = "./privateKeys/mimapkcs.key";
		BufferedReader br = new BufferedReader(new FileReader(keyPath));
		Security.addProvider(new BouncyCastleProvider());
		PEMParser pp = new PEMParser(br);
		PEMKeyPair pemKeyPair = (PEMKeyPair) pp.readObject();
		KeyPair kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
		pp.close();
		System.out.println("PRIVATE "+kp.getPrivate());
	}*/
	
}
