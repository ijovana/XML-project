package controller;
import ib.security.CertificateGenerator;
import ib.security.IssuerData;
import ib.security.SubjectData;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import view.EnterPasswordDialog;


public class SignCertificateAction extends AbstractAction{
	
	private String pass = "ca";
	private String name = "ca1";
	private PrivateKey privateKeyIssuer;
	private PublicKey publicKeyIssuer;
	private String password = "kr";
	private X509Certificate cacert;
	private String path;
	private String reqName;
	
	public static final String KEY_STORE_FILE = "./kljucevi/bezbednost.jks";
	public static final String CERT_REQUESTS = "./requests";
	
	public SignCertificateAction() {
		super();
		putValue(NAME, "Sign");
		putValue(SHORT_DESCRIPTION, "Sign request with chosen subordinate CA.");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		reqName = super.getValue("imeFajla").toString();
		path = super.getValue("zahtev").toString();
		JDialog d = (JDialog) super.getValue("parent");
		
		JComboBox cas = (JComboBox) super.getValue("odabraniCA");
		
		EnterPasswordDialog enterPasswordDialog = new EnterPasswordDialog(reqName, path, cas);
		enterPasswordDialog.setVisible(true);
		
		//JOptionPane.showMessageDialog(null, "Certificate signed.");
		
		d.dispose();
	}

}
