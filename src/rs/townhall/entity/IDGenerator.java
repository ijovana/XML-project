package rs.townhall.entity;

import java.util.UUID;

public class IDGenerator {
	
	public static String generateID() {
		return UUID.randomUUID().toString();
	}
	
	// Demonstration example
	public static void main(String args[]) {
		// Generate 10 unique id
		for(int i=0; i<10; i++)
			System.out.println(IDGenerator.generateID());
	}
}
