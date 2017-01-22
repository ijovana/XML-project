package controller;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;

public class OkCloseDialogAction extends AbstractAction {

	public OkCloseDialogAction()
	{
		super();
		putValue(NAME, "Ok");
		putValue(SHORT_DESCRIPTION, "Close dialog. ");	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		JDialog dialog = (JDialog) super.getValue("parent");
		dialog.dispose();	
	}

	
	
	
	
}
