package controller;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JPasswordField;

import app.MainFrame;


public class ConfirmPasswordAction extends AbstractAction{
	
	
	public ConfirmPasswordAction() {
		super();
		putValue(NAME, "Open file");
		putValue(SHORT_DESCRIPTION, "Sign some new requests for certificates.");
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		
		String path = (String) super.getValue("path");
		JPasswordField password = (JPasswordField) super.getValue("password");
		String pass = password.getText();
		
		MainFrame main = MainFrame.getInstance();
		 
        try {
			main.setJKSFile(path, pass);
			JDialog dialog = (JDialog) super.getValue("dialog");
			dialog.dispose();
			
		} catch (NoSuchAlgorithmException | CertificateException
				| KeyStoreException | NoSuchProviderException | IOException e1) {
			e1.printStackTrace();
			}
	}
	

}
