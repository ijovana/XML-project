package controller;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

import app.MainFrame;
import app.StoreCertificate;
import view.CreateSeeCertificateDialog;
import model.ClientData;


public class SaveCertificateAction extends AbstractAction {
	
	
	private CreateSeeCertificateDialog dialog;
	
	public SaveCertificateAction() {
		super();
		putValue(NAME, "Create");
		putValue(SHORT_DESCRIPTION, "Create subordinate CA. ");
	}
	
	public SaveCertificateAction(CreateSeeCertificateDialog dialog)
	{
		super();
		putValue(NAME, "Create");
		putValue(SHORT_DESCRIPTION, "Create subordinate CA. ");
		this.dialog = dialog;
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		
		ClientData clientData = new ClientData();
		clientData.setSigAlg(dialog.getSigAlg());
		clientData.setValidity(dialog.getValidity().getText().toString());
		clientData.setCommonName(dialog.getCommonName().getText().toString());
		clientData.setOrganizationUnit(dialog.getOrganizationUnit().getText().toString());
		clientData.setOrganizationName(dialog.getOrganizationName().getText().toString());
		clientData.setLocalityName(dialog.getLocalityName().getText().toString());
		clientData.setStateName(dialog.getStateName().getText().toString());
		clientData.setCountry(dialog.getCountry().getText().toString());
		clientData.setEmail(dialog.getEmail().getText().toString());
		clientData.setPassword(dialog.getPassword().getText());
		
		StoreCertificate storeCert = new StoreCertificate();
		
		try {
			storeCert.storeCertificateInJKS(clientData);
			JDialog d = (JDialog) super.getValue("parent");
			d.dispose();
			//TODO: decide how to reload current file
			//MainFrame instance = MainFrame.getInstance();
			//String path = instance.getCurrentKeystorePath();
			//instance.setJKSFile(path, password);			
		} catch (KeyStoreException | NoSuchProviderException
				| NoSuchAlgorithmException | CertificateException | IOException e1) {
			e1.printStackTrace();
		}
	}
}
