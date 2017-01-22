package app;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.border.Border;

import controller.ExportCertificateAction;
import controller.GenKeyButton;
import controller.OpenExistingFileAction;
import controller.TableController;
import model.SertTableModel;


public class MainFrame extends JFrame {
	
	private JTable table;
	private SertTableModel tableModel;
	private String currentOpenedPath;

	private MainFrame()
	{
		table = new JTable();
		tableModel = new SertTableModel();
		currentOpenedPath = "";
	}
	
	private static MainFrame instance = null;
	
	public static MainFrame getInstance()
	{
		if (instance != null)
			return instance;
		else{
			instance = new MainFrame();
			try {
				instance.initialise();
			} catch (NoSuchAlgorithmException | CertificateException
					| KeyStoreException | NoSuchProviderException | IOException e) {
				e.printStackTrace();
			}
			return instance;
		}
	}
	
	
	private void initialise() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException, IOException
	{
		Toolkit myKit = Toolkit.getDefaultToolkit();
		Dimension screenDimension = myKit.getScreenSize();
		
		this.setSize(screenDimension.width*2/3, screenDimension.height*2/3);
		setTitle("Generate certificate");
		this.setLocationRelativeTo(null);
	
		setButtons();
		addRequestsTable();
	}
	
	public void setButtons()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));	
		
		GenKeyButton keyButton = new GenKeyButton();
		JButton createCert = new JButton(keyButton);
		createCert.setPreferredSize(new Dimension(150, 24));	
		OpenExistingFileAction openFile = new OpenExistingFileAction();
		JButton openFileButton = new JButton(openFile);
		openFileButton.setPreferredSize(new Dimension(150, 24));
		ExportCertificateAction export = new ExportCertificateAction();
		JButton exportCertButton = new JButton(export);
		toolBar.add(createCert);
		toolBar.add(openFileButton);	
		toolBar.add(exportCertButton);
		toolBar.setFloatable(false);
		toolBar.setVisible(true);
		this.add(toolBar, BorderLayout.NORTH);
	}
	
	
	public void addRequestsTable() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException, IOException
	{
        JPanel panel = new JPanel(new BorderLayout());
		tableModel = new SertTableModel("./requests", "");
		Vector<String> columns = new Vector<String>();
		columns.add("Subject common name");
		tableModel.setColumns(columns);
		tableModel.fillData();
		table = new JTable(tableModel);
		table.getTableHeader().setReorderingAllowed(false);
	    table.getTableHeader().setResizingAllowed(true);
	    table.setShowGrid(false);
	    table.setIntercellSpacing(new Dimension(0, 0));
	    TableController list = new TableController();
	    table.addMouseListener(list);
	    panel.add(table, BorderLayout.CENTER);
	    this.add(panel, BorderLayout.CENTER);
	}
	
	public void clearTable()
	{
		SertTableModel model = (SertTableModel) table.getModel();
		model.clearData();
	}

	public void removeTable()
	{
		this.remove(table);
	}

	public void setJKSFile(String path, String password) throws NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException, IOException
	{
		this.currentOpenedPath = path;
		SertTableModel model = new SertTableModel(path, password);
		Vector<String> columns = new Vector<String>();
		columns.add("Aliases in JKS");
		model.setColumns(columns);
		model.fillData();
	    table.setModel(model);    
	}

	public String getCurrentKeystorePath() {
		return currentOpenedPath;
	}


	public void setCurrentKeystorePath(String currentKeystorePath) {
		this.currentOpenedPath = currentKeystorePath;
	}


	public JTable getTable() {
		return table;
	}


	public void setTable(JTable table) {
		this.table = table;
	}


	public SertTableModel getTableModel() {
		return tableModel;
	}


	public void setTableModel(SertTableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	
}
