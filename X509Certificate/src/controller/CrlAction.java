package controller;

import ib.util.CrlUtil;

import java.awt.event.ActionEvent;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CRLException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.security.auth.x500.X500Principal;
import javax.swing.AbstractAction;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLNumber;
import org.bouncycastle.asn1.x509.CRLReason;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V2CRLGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;

import app.MainFrame;

public class CrlAction extends AbstractAction{
	
	private static final String PRIVATE_KEY_PATH = "C:/Users/Mima/Desktop/X509Certificate/privateKeys/arhiv.key";
	private static final String CRL_PATH = "C:/Users/Mima/Desktop/X509Certificate/data/povuceniSert.crl";
	
	
	 static {
	    	//staticka inicijalizacija
	        Security.addProvider(new BouncyCastleProvider());
	        org.apache.xml.security.Init.init();
	    }
	
	private X509Certificate cert;
	X509V2CRLGenerator   crlGen = new X509V2CRLGenerator();
	Date                 now = new Date();
	PrivateKey           caCrlPrivateKey = CrlUtil.readPrivateKey(PRIVATE_KEY_PATH);
	X509CRL existingCrl = CrlUtil.openFromFile(CRL_PATH); 
	
	
	public CrlAction(){
		super();
		putValue(NAME, "Revocate");
		putValue(SHORT_DESCRIPTION, "Add certificates to jks list.");
	}
	
	public CrlAction(X509Certificate cert){
		super();
		putValue(NAME, "Revocate");
		putValue(SHORT_DESCRIPTION, "Add certificates to jks list.");
		this.cert = cert;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void actionPerformed(ActionEvent e) {
		
		MainFrame main = MainFrame.getInstance();

		crlGen.setIssuerDN(cert.getIssuerX500Principal()); 
		crlGen.setThisUpdate(now);
		crlGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		try {
			if (existingCrl!=null)
				crlGen.addCRL(existingCrl);
		} catch (CRLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		 
		try {
			
			crlGen.addCRLEntry(cert.getSerialNumber(), now, CRLReason.UNSPECIFIED);
			
			X509CRL crl = crlGen.generateX509CRL(caCrlPrivateKey, "BC");
			
			CrlUtil.saveCRLfile(CRL_PATH, crl);
		
		} catch (InvalidKeyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchProviderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SignatureException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
	}
}
