package controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class CloseDialogAction extends AbstractAction{

	public CloseDialogAction() {
		super();
		putValue(NAME, "Cancel");
		putValue(SHORT_DESCRIPTION, "Close dialog. ");	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = (JDialog) super.getValue("parent");
		dialog.dispose();
		
	}
	
	

}
