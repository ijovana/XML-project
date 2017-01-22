package controller;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import view.CreateSeeCertificateDialog;


public class GenKeyButton extends AbstractAction{

	public GenKeyButton() {
		super();
		putValue(NAME, "Generate CA");
		putValue(SHORT_DESCRIPTION, "Sign some new requests for certificates.");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		tryGeneratingCertificate();		
	}
	
	
	private void tryGeneratingCertificate(){
		try
		{
			CreateSeeCertificateDialog dialog = new CreateSeeCertificateDialog();
			dialog.setVisible(true);
			
		}catch(Exception e)
		{
			 e.printStackTrace();
		}
	}
}
