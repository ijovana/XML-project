package view;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dialog.ModalityType;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import controller.ChooseCertFileAction;
import controller.CloseDialogAction;
import controller.ConfirmPasswordAction;


public class PasswordJKSDialog extends JDialog{
	
	private JPasswordField password;
	private String path;
	private String reqName;
	private char[] enteredPassword;
	private JComboBox cas;
	private String selectedFile;
	
	private EnterPasswordDialog parent;
	
	
	public PasswordJKSDialog(String path, String reqName, JComboBox cas, char[] enteredPassword, String selectedPath)
	{	
		this.password = new JPasswordField(10);
		this.reqName = reqName;
		this.cas = cas;
		this.path = path;
		this.selectedFile = selectedPath;
		this.enteredPassword = enteredPassword;
		
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    JPanel panel = new JPanel(new BorderLayout());
	    BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
	    panel.setLayout(layout);
	    
		placeComponentsAndCert(panel);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}
	
	public PasswordJKSDialog(String path)
	{
		this.password = new JPasswordField(10);
		this.path = path;
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    JPanel panel = new JPanel(new BorderLayout());
	    BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
	    panel.setLayout(layout);
	    
		placeComponents(panel);
		
		this.pack();
		this.setLocationRelativeTo(null);
	}

	public void placeComponents(JPanel panel)
	{
		JLabel enterPass = new JLabel("Enter your password: ");
		ConfirmPasswordAction okAction = new ConfirmPasswordAction();
		okAction.putValue("password", password);
		okAction.putValue("path", this.path);
		okAction.putValue("dialog", this);
		JButton okButton = new JButton(okAction);
		
		CloseDialogAction closeAction = new CloseDialogAction();
		closeAction.putValue("parent", this);
		JButton cancelButton = new JButton(closeAction);
	
		
		int panNum=2;
		JPanel[] panels = new JPanel[panNum];
		
		for(int i=0; i<panNum; i++)
		{
			panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		
		panels[0].add(enterPass);
		panels[0].add(this.password);
		panels[1].add(okButton);
		panels[1].add(cancelButton);
		
		
		for(int i=0; i<panNum; i++)
		{
			panel.add(panels[i]);		
		}
		
		this.add(panel);	
	}
	
	public void placeComponentsAndCert(JPanel panel){
		JLabel enterPass = new JLabel("Enter your password: ");
		ChooseCertFileAction okAction = new ChooseCertFileAction();
		okAction.putValue("dialog", this);
		okAction.putValue("zahtev", path);
		okAction.putValue("imeFajla", reqName);
		okAction.putValue("odabraniCA", cas);
		okAction.putValue("password", password);
		okAction.putValue("parent", this);
		okAction.putValue("selectedPath", this.selectedFile);
		okAction.putValue("enteredPassword", this.enteredPassword);
		//System.out.println("DIJALOGU PROSLEDNJENO: "+path+" "+reqName+" "+cas.getSelectedItem().toString()+" "+password.getPassword()+" "+selectedFile);
		
		JButton okButton = new JButton(okAction);
		CloseDialogAction closeAction = new CloseDialogAction();
		closeAction.putValue("parent", this);
		JButton cancelButton = new JButton(closeAction);
	
		
		int panNum=2;
		JPanel[] panels = new JPanel[panNum];
		
		for(int i=0; i<panNum; i++)
		{
			panels[i] = new JPanel(new FlowLayout(FlowLayout.LEFT));
		}
		
		panels[0].add(enterPass);
		panels[0].add(this.password);
		panels[1].add(okButton);
		panels[1].add(cancelButton);
		
		
		for(int i=0; i<panNum; i++)
		{
			panel.add(panels[i]);		
		}
		
		this.add(panel);	
		
		
		
	}
	
}
