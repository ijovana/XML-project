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
import javax.swing.JTextField;

import controller.OkPasswordAction;


public class EnterPasswordDialog extends JDialog{

	private JPasswordField password;
	private String reqName;
	private String path;
	private JComboBox chooseCA;
	
	
	public EnterPasswordDialog(String reqName, String path, JComboBox chooseCA)
	{
		this.reqName = reqName;
		this.path = path;
		this.chooseCA = chooseCA;
		this.password = new JPasswordField(10);
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    JPanel panel = new JPanel(new BorderLayout());
	    BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
	    panel.setLayout(layout);
	    
		placeComponents(panel);
		
		this.pack();
		this.setLocationRelativeTo(null);
		
	}
	
	private void placeComponents(JPanel panel)
	{
		JLabel enterPass = new JLabel("Enter password: ");
		OkPasswordAction okAction = new OkPasswordAction();
		okAction.putValue("zahtev", path);
		okAction.putValue("imeFajla", reqName);
		okAction.putValue("odabraniCA", chooseCA);
		okAction.putValue("password", password);
		okAction.putValue("parent", this);
		
		JButton okButton = new JButton(okAction);
		JButton cancelButton = new JButton("Cancel");
		
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
