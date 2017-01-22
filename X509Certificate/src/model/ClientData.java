package model;
import javax.swing.JTextField;


public class ClientData {
	
	private String sigAlg;
	private String validity;
	private String commonName;
	private String organizationUnit;
	private String organizationName;
	private String localityName;
	private String stateName; 
	private String country; // two letter country code
	private String email;
	private String password;
	
	public ClientData()
	{	
	}
	
	public ClientData(String sigAlg, String validity, String commonName, String organizationUnit,
					  String organizationName, String localityName, String stateName, String country, String email, String password)
	{
		this.sigAlg = sigAlg;
		this.validity = validity;
		this.commonName = commonName;
		this.organizationUnit = organizationUnit;
		this.organizationName = organizationName;
		this.localityName = localityName;
		this.stateName = stateName;
		this.country = country;
		this.email = email;
		this.password = password;
		
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSigAlg() {
		return sigAlg;
	}

	public void setSigAlg(String sigAlg) {
		this.sigAlg = sigAlg;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getOrganizationUnit() {
		return organizationUnit;
	}

	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}

	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
	}

	public String getLocalityName() {
		return localityName;
	}

	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}

