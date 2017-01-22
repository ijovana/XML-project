package controller;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import app.MainFrame;
import view.PasswordJKSDialog;


public class OpenExistingFileAction extends AbstractAction {

	public OpenExistingFileAction() {
		super();
		putValue(NAME, "Open file");
		putValue(SHORT_DESCRIPTION, "Sign some new requests for certificates.");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		MainFrame main = MainFrame.getInstance();
		
		 JFileChooser fileChooser = new JFileChooser();
		 fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	        int returnValue = fileChooser.showOpenDialog(null);
	        if (returnValue == JFileChooser.APPROVE_OPTION) {
	          File selectedFile = fileChooser.getSelectedFile();
	          System.out.println("Selected file "+selectedFile.getName());
	          System.out.println("Path "+selectedFile.getAbsolutePath());
	          String path = selectedFile.getAbsolutePath();
	          
	          main.clearTable();
	          String password = null;
	  
	          PasswordJKSDialog dialog = new PasswordJKSDialog(path);
	          dialog.setVisible(true);
		
	        }
	
	}
	
	
}
