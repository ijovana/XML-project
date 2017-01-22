package rs.townhall.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class Hash {
	
	// Salt can be random, but for simplicity is used constant
	private static final String SALT = "_%Town-Hall%_";
	
	private static MessageDigest crypt;
	static {
		try {
			crypt = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			System.out.println(e.getMessage());
		}	
	}
	
	public static String hash(String value) throws UnsupportedEncodingException {
		String hashedvalue = null;
		crypt.reset();
        crypt.update(new String(value + SALT).getBytes("UTF-8"));
        hashedvalue = byteToHex(crypt.digest());
		
		return hashedvalue;
	}
	
	private static String byteToHex(final byte[] hash) {
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
	
	// Demonstration purpose
	public static void main(String[] args){
		String password = "My5tr0ng_Pa55w0rd";
		System.out.println("Password: " + password);
		System.out.println("Salt: " + SALT);
		try {
			String hashedPassword = hash(password);
			System.out.println("Hashed password: " + hashedPassword);
		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		}
	}
}
