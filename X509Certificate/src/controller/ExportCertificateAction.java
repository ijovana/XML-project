package controller;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import app.MainFrame;
import model.SertTableModel;


public class ExportCertificateAction extends AbstractAction{

	public ExportCertificateAction(){
		super();
		putValue(NAME, "Export certificate");
		putValue(SHORT_DESCRIPTION, "Confirm password. ");
	}	
	@Override
	public void actionPerformed(ActionEvent e){
		
		MainFrame main = MainFrame.getInstance();
		JTable table = MainFrame.getInstance().getTable();
		SertTableModel model = (SertTableModel) table.getModel();
	    int index = table.getSelectedRow();
		  
		if(index<0) return;
		   
		String selValue = (String) model.getValueAt(index, 0);
	
		boolean isAlias = model.aliasExists(selValue);
		
		   if(isAlias==true)
		   {
			   X509Certificate cert = model.getCertificate(selValue);
			 
			   JFileChooser saveCertificateChooser = new JFileChooser();
			   int returnValue = saveCertificateChooser.showSaveDialog(null);
			   if (returnValue == JFileChooser.APPROVE_OPTION) {
				   File selectedFile = saveCertificateChooser.getSelectedFile();
			       System.out.println("Selected file "+selectedFile.getName());
			       String path = selectedFile.getAbsolutePath()+".crt";
			       System.out.println("Path "+path);
			       
			       try {
					FileOutputStream fos = new FileOutputStream(path);
					fos.write( cert.getEncoded() );
					fos.flush();
					fos.close();
				
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (CertificateEncodingException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			       
			   }			   
		   }	
	}
}
