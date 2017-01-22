package model;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import javax.naming.ldap.HasControls;
import javax.swing.table.AbstractTableModel;

import org.bouncycastle.jcajce.provider.digest.GOST3411.HashMac;


public class SertTableModel extends AbstractTableModel{

	private Vector<Vector> data;
	private Vector<String> columns; 
	private String fileName;
	private String jksPass;
	private KeyStore keystore;
	private HashMap<String,X509Certificate> certificatesHash;
	
	public SertTableModel()
	{
		this.data = new Vector<Vector>();
		this.columns = new Vector<String>();
		this.fileName = "";
		this.jksPass = "";
		this.certificatesHash = new HashMap<String, X509Certificate>();
		this.keystore = null;
	}
	
	public SertTableModel(String fileName, String jksPass)
	{
		this.data = new Vector<Vector>();
		this.columns = new Vector<String>();
		this.fileName = fileName;
		this.jksPass = jksPass;
		this.certificatesHash = new HashMap<String, X509Certificate>();
		this.keystore = null;
	}
	
	public void fillData() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException, IOException
	{
		data.clear();		
		ArrayList fileNames = new ArrayList<String>();
		fileNames = getFileNames();
		
		int columnsNumber = columns.size();
		
		for(int i=0; i<fileNames.size(); i++)
		{
			Vector<String> row = new Vector<String>();
		    for(int j=0; j<columnsNumber; j++)
			{
				row.add(fileNames.get(i).toString());
			}
			data.add(row);
		}
	}
	
	
	public ArrayList<String> getFileNames() throws NoSuchAlgorithmException, CertificateException, KeyStoreException, NoSuchProviderException, IOException
	{
		ArrayList<String> fileNames = new ArrayList<String>();
		File f = new File(this.fileName);
		File[] listOfFiles=null;
		if(f.isDirectory()==true){
			listOfFiles = f.listFiles();
			 for (int i = 0; i < listOfFiles.length; i++) {
				 if (listOfFiles[i].isFile()) {
				    System.out.println("File " + listOfFiles[i].getName());
				    fileNames.add(listOfFiles[i].getName().toString());
				    }
			   }	
		}
		
		if(f.isFile()==true)
		{
			String ext = getFileExtension(f.getName());
			System.out.println("It's file with extension "+ext);
			if(ext.equals("jks"))
			{
				fileNames = readJKSAndSetCerts(f);
			}
		}
	   return fileNames;
	   
	}
	
	public ArrayList readJKSAndSetCerts(File f) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, NoSuchProviderException
	{
		ArrayList<String> list = new ArrayList<String>();
		
		keystore = KeyStore.getInstance("JKS", "SUN");
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.fileName));
		keystore.load(in, jksPass.toCharArray());
		Enumeration<String> aliases = keystore.aliases();
		
		while(aliases.hasMoreElements()){
			String param = (String) aliases.nextElement();
			System.out.println(param);
			list.add(param);
			Certificate cert = keystore.getCertificate(param.toString());
			certificatesHash.put(param, (X509Certificate) cert);
		}
		
		return list;
	}
	
	public boolean aliasExists(String alias)
	{
		if(this.keystore==null)
		{
			return false;
		}
		
		try {
			return keystore.containsAlias(alias);
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public X509Certificate getCertificate(String alias)
	{
		X509Certificate cert = certificatesHash.get(alias);
		return cert;
	}
	
	public String getJksPass() {
		return jksPass;
	}

	public void setJksPass(String jksPass) {
		this.jksPass = jksPass;
	}

	public HashMap<String, X509Certificate> getCertificatesHash() {
		return certificatesHash;
	}

	public void setCertificatesHash(
			HashMap<String, X509Certificate> certificatesHash) {
		this.certificatesHash = certificatesHash;
	}

	public String getFileExtension(String fi)
	{
		String extension = "";

		int i = fi.lastIndexOf('.');
		if (i > 0) {
		    extension = fi.substring(i+1);
		}
		
		return extension;
	}
	
	public void clearData()
	{
		this.data.clear();
		this.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data.get(rowIndex).get(columnIndex);
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return false;
	}


    public void setColumns(Vector<String> columns){
		this.columns = columns;
	}

	public Vector<Vector> getData() {
		return data;
	}

	public void setData(Vector<Vector> data) {
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Vector<String> getColumns() {
		return columns;
	}
    
}
