package controller;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.Set;

import javax.swing.JTable;

import app.MainFrame;
import view.CreateSeeCertificateDialog;
import view.RequestViewDialog;
import model.SertTableModel;


public class TableController implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent e) {
	   if(e.getClickCount()==2)
	   {
		   JTable table = MainFrame.getInstance().getTable();
		   SertTableModel model = (SertTableModel) table.getModel();
		   int index = table.getSelectedRow();
		  
		   if(index<0) return;
		   
		   String selValue = (String) model.getValueAt(index, 0);

		   boolean isAlias = model.aliasExists(selValue);
		  
		   if(isAlias==false){
			   openRequestDialog(selValue);
		   }
		   else
		   {
			   X509Certificate cert = model.getCertificate(selValue);
			   CreateSeeCertificateDialog dialog = new CreateSeeCertificateDialog(cert);
			   dialog.setVisible(true);;
		   }	   
	   }
	}
	
	
	
	public void openRequestDialog(String selValue)
	{
		RequestViewDialog dialog;
		   
		try {
			dialog = new RequestViewDialog(selValue);
			dialog.setVisible(true);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException
				| IOException e1) {
			e1.printStackTrace();
		} catch (CertificateException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (NoSuchProviderException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
