package view;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.symmetric.util.BaseStreamCipher;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;

import controller.SignCertificateAction;


public class RequestViewDialog extends JDialog{
	
	public static final String CA = "./kljucevi/bezbednost.jks";
	public static final String REQUESTS_FILE = "./requests";
	public static final String pass = "kr";
	
	public RequestViewDialog()
	{
	}
	
	public RequestViewDialog(String reqName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, KeyStoreException, NoSuchProviderException
	{ 
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    JPanel panel = new JPanel(new BorderLayout());
	    BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
	    panel.setLayout(layout);
	    
		placeComponents(panel, reqName);
		
		this.pack();
		this.setLocationRelativeTo(null);

	}
	
	
	public void placeComponents(JPanel panel, String reqName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, KeyStoreException, NoSuchProviderException
	{

		String path = REQUESTS_FILE+"/"+reqName;
		PEMParser pars = new PEMParser(new FileReader(path));
		Object obj = pars.readObject();
		
        PKCS10CertificationRequest csr = (PKCS10CertificationRequest)obj;
        SubjectPublicKeyInfo publicKey =  csr.getSubjectPublicKeyInfo();
       // System.out.println(publicKey.getPublicKeyData().getString());
       
        //TODO: VRLO SUMNJIVO..
        RSAKeyParameters rsa = (RSAKeyParameters) PublicKeyFactory.createKey(publicKey);
        RSAPublicKeySpec rsaSpec = new RSAPublicKeySpec(rsa.getModulus(), rsa.getExponent());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey subjectPublicKey = kf.generatePublic(rsaSpec);
        
        X500Name name = csr.getSubject();
        RDN rdn = name.getRDNs(BCStyle.CN)[0];
        String cn = IETFUtils.valueToString(rdn.getFirst().getValue());
        rdn = name.getRDNs(BCStyle.O)[0];
        String on = IETFUtils.valueToString(rdn.getFirst().getValue());
        rdn = name.getRDNs(BCStyle.OU)[0];
        String ou = IETFUtils.valueToString(rdn.getFirst().getValue());
        rdn = name.getRDNs(BCStyle.L)[0];
        String l = IETFUtils.valueToString(rdn.getFirst().getValue());
        rdn = name.getRDNs(BCStyle.ST)[0];
        String sn = IETFUtils.valueToString(rdn.getFirst().getValue());
        rdn = name.getRDNs(BCStyle.C)[0];
        String c = IETFUtils.valueToString(rdn.getFirst().getValue());
        rdn = name.getRDNs(BCStyle.E)[0];
        String e = IETFUtils.valueToString(rdn.getFirst().getValue());
        //System.out.println("PODACI "+cn+" "+on+" "+ou+" "+l+" "+sn+" "+c+" "+e );
        
        int panNum = 10;
        JPanel[] panels = new JPanel[panNum];
        
        JLabel details = new JLabel("REQUEST DETAILS: ");
		JLabel commonNameLabel = new JLabel("Common Name (CN):");
		JLabel organizationUnitLabel = new JLabel("Organization Unit (OU:)");
		JLabel organizationNameLabel = new JLabel("Organization Name (O):");
		JLabel localityNameLabel = new JLabel("Locality Name (L):");
		JLabel stateLabel = new JLabel("State Name (ST):");
		JLabel countryLabel = new JLabel("Country (C):");
		JLabel emailLabel = new JLabel("Email (E):");
		
		
		JTextField commonName = new JTextField(10);
		commonName.setText(cn);
		commonName.setEditable(false);
		JTextField organizationUnit = new JTextField(10);
		organizationUnit.setText(ou);
		organizationUnit.setEditable(false);
		JTextField organizationName = new JTextField(10);
		organizationName.setText(on);
		organizationName.setEditable(false);
		JTextField localityName = new JTextField(10);
		localityName.setText(l);
		localityName.setEditable(false);
		JTextField stateName = new JTextField(10);
		stateName.setText(sn);
		stateName.setEditable(false);
		JTextField country = new JTextField(10);
		country.setText(c);
		country.setEditable(false);
		JTextField email = new JTextField(10);
		email.setText(e);
		email.setEditable(false);

		for(int i=0; i<panNum; i++)
		{
			panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		
		panels[0].add(details);
		panels[1].add(commonNameLabel);
		panels[2].add(organizationUnitLabel);
		panels[3].add(organizationNameLabel);
		panels[4].add(localityNameLabel);
		panels[5].add(stateLabel);
		panels[6].add(countryLabel);
		panels[7].add(emailLabel);
		
		
		panels[1].add(commonName);
		panels[2].add(organizationUnit);
		panels[3].add(organizationName);
		panels[4].add(localityName);
		panels[5].add(stateName);
		panels[6].add(country);
		panels[7].add(email);
	
		
	    Vector comboBoxItems=new Vector();	
	    comboBoxItems = getCAs();
		final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
		JComboBox chooseCA = new JComboBox(model);
		chooseCA.setSelectedIndex(0);
		
		JLabel choose = new JLabel("Choose CA to sign certificate: ");
		panels[8].add(choose);
		panels[8].add(chooseCA);
	
		SignCertificateAction sign = new SignCertificateAction();
		sign.putValue("zahtev", path);
		sign.putValue("imeFajla", reqName);
		sign.putValue("odabraniCA", chooseCA);
		sign.putValue("parent", this);
		
		JButton signCertificate = new JButton(sign);
		panels[9].add(signCertificate);
		
		for(int i=0; i<panNum; i++)
		{
			panel.add(panels[i]);		
		}

        this.add(panel);
	}
	
	
	public Vector getCAs() throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, NoSuchProviderException
	{
		Vector<String> list = new Vector();
		
		KeyStore ks = KeyStore.getInstance("JKS", "SUN");
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(CA));
		ks.load(in, pass.toCharArray());
		Enumeration<String> aliases = ks.aliases();
		
		while(aliases.hasMoreElements()){
			String param = (String) aliases.nextElement();
			System.out.println(param);
			list.add(param);
		}
		
		return list;
	}
	
	
}
