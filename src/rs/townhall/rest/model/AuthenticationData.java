package rs.townhall.rest.model;

import java.io.UnsupportedEncodingException;

import javax.xml.bind.annotation.XmlRootElement;

import rs.townhall.security.Hash;

@XmlRootElement
public class AuthenticationData {

	private String username;
	private String password;
	
	public AuthenticationData(){}
	
	public AuthenticationData(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		try {
			return Hash.hash(password);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
