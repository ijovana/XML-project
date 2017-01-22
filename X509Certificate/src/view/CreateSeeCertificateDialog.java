package view;
import java.awt.FlowLayout;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import controller.CloseDialogAction;
import controller.CrlAction;
import controller.OkCloseDialogAction;
import controller.SaveCertificateAction;


public class CreateSeeCertificateDialog extends JDialog {
	
	private int jtextfieldLength = 20;
	private String dialogName = "Generate certificate";
	private JTextField validity = new JTextField(jtextfieldLength);
	private JTextField commonName = new JTextField(jtextfieldLength);
	private JTextField organizationUnit = new JTextField(jtextfieldLength);
	private JTextField organizationName = new JTextField(jtextfieldLength);
	private JTextField localityName = new JTextField(jtextfieldLength);
	private JTextField stateName = new JTextField(jtextfieldLength); 
	private JTextField country = new JTextField(jtextfieldLength); // two letter country code
	private JTextField email = new JTextField(jtextfieldLength);
	private JComboBox chooseSigAlg;
	private JPasswordField password = new JPasswordField(jtextfieldLength);
	private X509Certificate showCertificate;
	
	public CreateSeeCertificateDialog(X509Certificate certificate)
	{
		this.showCertificate = certificate;
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel.setLayout(layout);
		
		this.add(panel);
		fillComponents(panel);

		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	
	public CreateSeeCertificateDialog()
	{	
		JPanel panel = new JPanel();
		BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel.setLayout(layout);
		
		this.add(panel);
		placeComponents(panel);

		this.pack();
		this.setLocationRelativeTo(null);
		
	}
	
	
	public void fillComponents(JPanel panel)
	{
		OkCloseDialogAction okAction = new OkCloseDialogAction();
		okAction.putValue("parent", this);
		JButton ok = new JButton(okAction);
		CloseDialogAction closeAction = new CloseDialogAction();
		closeAction.putValue("parent", this);
		JButton cancel = new JButton(closeAction);
		CrlAction crl = new CrlAction(showCertificate);
		crl.putValue("parent", showCertificate);
		JButton add = new JButton(crl);
		
	    JPanel[] panels = new JPanel[11];
		
	    SpringLayout lay = new SpringLayout();
		for(int i=0; i<=10; i++)
		{
			panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		
		JLabel sigAlgLabel = new JLabel("Signature Algorithm:");
		JLabel validityLabel = new JLabel("Validity (days):");
		JLabel commonNameLabel = new JLabel("Common Name (CN):");
		JLabel organizationUnitLabel = new JLabel("Organization Unit (OU:)");
		JLabel organizationNameLabel = new JLabel("Organization Name (O):");
		JLabel localityNameLabel = new JLabel("Locality Name (L):");
		JLabel stateLabel = new JLabel("State Name (ST):");
		JLabel emailLabel = new JLabel("Email (E):");
		
		panels[0].add(sigAlgLabel);
		panels[1].add(validityLabel);
		panels[2].add(commonNameLabel);
		panels[3].add(organizationUnitLabel);
		panels[4].add(organizationNameLabel);
		panels[5].add(localityNameLabel);
		panels[6].add(stateLabel);
		panels[7].add(emailLabel);
		panels[10].add(ok);
		panels[10].add(cancel);
		panels[10].add(add);
		
		String[] algorithms = { "SHA256withRSA"};
		chooseSigAlg = new JComboBox(algorithms);
		chooseSigAlg.setSelectedIndex(0);
		
		X500Name name = null;
		try {
			name = new JcaX509CertificateHolder(showCertificate).getSubject();
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
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
        
        String val = showCertificate.getNotAfter().toString();
		
		this.validity.setText(val);
		this.validity.setEditable(false);
		this.commonName.setText(cn);
		this.commonName.setEditable(false);
		this.organizationUnit.setText(ou);
		this.organizationUnit.setEditable(false);
		this.organizationName.setText(on);
		this.organizationName.setEditable(false);
		this.localityName.setText(l);
		this.localityName.setEditable(false);
		this.stateName.setText(sn);
		this.stateName.setEditable(false);
		this.email.setText(e);
		this.email.setEditable(false);
		
		panels[0].add(chooseSigAlg);
		panels[1].add(this.validity);
		panels[2].add(this.commonName);
		panels[3].add(this.organizationUnit);
		panels[4].add(this.organizationName);
		panels[5].add(this.localityName);
		panels[6].add(this.stateName);
		panels[7].add(this.email);
	
		
		for(int i=0; i<11; i++)
		{
			panel.add(panels[i]);		
		}
		
	}

	public void placeComponents(JPanel panel)
	{
		SaveCertificateAction saveAction = new SaveCertificateAction(this);
		saveAction.putValue("parent", this);
		JButton save = new JButton(saveAction);
		
		CloseDialogAction closeAction = new CloseDialogAction();
		closeAction.putValue("parent", this);
		JButton cancel = new JButton(closeAction);
		
		
	    JPanel[] panels = new JPanel[11];
		
	    SpringLayout lay = new SpringLayout();
		for(int i=0; i<=10; i++)
		{
			panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		
		JLabel sigAlgLabel = new JLabel("Signature Algorithm:");
		JLabel validityLabel = new JLabel("Validity (days):");
		JLabel commonNameLabel = new JLabel("Common Name (CN):");
		JLabel organizationUnitLabel = new JLabel("Organization Unit (OU:)");
		JLabel organizationNameLabel = new JLabel("Organization Name (O):");
		JLabel localityNameLabel = new JLabel("Locality Name (L):");
		JLabel stateLabel = new JLabel("State Name (ST):");
		JLabel countryLabel = new JLabel("Country (C):");
		JLabel emailLabel = new JLabel("Email (E):");
		JLabel passLabel = new JLabel("PK Password:");
		
		panels[0].add(sigAlgLabel);
		panels[1].add(validityLabel);
		panels[2].add(commonNameLabel);
		panels[3].add(organizationUnitLabel);
		panels[4].add(organizationNameLabel);
		panels[5].add(localityNameLabel);
		panels[6].add(stateLabel);
		panels[7].add(countryLabel);
		panels[8].add(emailLabel);
		panels[9].add(passLabel);
		panels[10].add(save);
		panels[10].add(cancel);
		
		String[] algorithms = { "SHA256withRSA"};
		chooseSigAlg = new JComboBox(algorithms);
		chooseSigAlg.setSelectedIndex(0);
		
		
		panels[0].add(chooseSigAlg);
		panels[1].add(this.validity);
		panels[2].add(this.commonName);
		panels[3].add(this.organizationUnit);
		panels[4].add(this.organizationName);
		panels[5].add(this.localityName);
		panels[6].add(this.stateName);
		panels[7].add(this.country);
		panels[8].add(this.email);
		panels[9].add(this.password);
	
		
		for(int i=0; i<11; i++)
		{
			panel.add(panels[i]);		
		}
		
		
	}
	
	public String getSigAlg()
	{
		int index = chooseSigAlg.getSelectedIndex();
		return chooseSigAlg.getSelectedObjects().toString();
	}
	
	public String getDialogName() {
		return dialogName;
	}

	public void setDialogName(String dialogName) {
		this.dialogName = dialogName;
	}

	public JTextField getValidity() {
		return validity;
	}

	public void setValidity(JTextField validity) {
		this.validity = validity;
	}

	public JTextField getCommonName() {
		return commonName;
	}

	public void setCommonName(JTextField commonName) {
		this.commonName = commonName;
	}

	public JTextField getOrganizationUnit() {
		return organizationUnit;
	}

	public void setOrganizationUnit(JTextField organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public JTextField getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(JTextField organizationName) {
		this.organizationName = organizationName;
	}

	public JTextField getLocalityName() {
		return localityName;
	}

	public void setLocalityName(JTextField localityName) {
		this.localityName = localityName;
	}

	public JTextField getStateName() {
		return stateName;
	}

	public void setStateName(JTextField stateName) {
		this.stateName = stateName;
	}

	public JTextField getCountry() {
		return country;
	}

	public void setCountry(JTextField country) {
		this.country = country;
	}

	public JTextField getEmail() {
		return email;
	}

	public void setEmail(JTextField email) {
		this.email = email;
	}
	
	public void setPassword(JPasswordField password){
		this.password = password;
	}
	
	public JPasswordField getPassword()
	{
		return password;
	}

}
