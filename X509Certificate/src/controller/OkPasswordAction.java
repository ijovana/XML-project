package controller;
import ib.security.CertificateGenerator;
import ib.security.IssuerData;
import ib.security.SubjectData;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.File;
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
import javax.swing.JFileChooser;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
import view.PasswordJKSDialog;


public class OkPasswordAction extends AbstractAction{

	private PrivateKey privateKeyIssuer;
	private PublicKey publicKeyIssuer;
	private String password = "kr";
	private X509Certificate cacert;
	private String path;
	private String reqName;
	private char[] enteredPassword;
	private EnterPasswordDialog parent;
	
	public static final String KEY_STORE_FILE = "./kljucevi/bezbednost.jks";
	public static final String CERT_REQUESTS = "./requests"; 
	
	
	public OkPasswordAction(){
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
		parent = (EnterPasswordDialog) super.getValue("parent");
		enteredPassword = p.getPassword();
		
		//TODO: where to save certificate??
		 JFileChooser fileChooser = new JFileChooser();
	     int returnValue = fileChooser.showOpenDialog(null);
	     if (returnValue == JFileChooser.APPROVE_OPTION) {
	        File selectedFile = fileChooser.getSelectedFile();
	        System.out.println("Selected file "+selectedFile.getName());
	        System.out.println("Path "+selectedFile.getAbsolutePath());

	        PasswordJKSDialog dialog = new PasswordJKSDialog(path, reqName, cas, enteredPassword, selectedFile.getAbsolutePath());
	        dialog.setVisible(true);
	        
	        JDialog d = (JDialog) super.getValue("parent");
	        d.dispose();
	     }
	}
}
