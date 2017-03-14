package tests;

import static org.junit.Assert.*;

import org.junit.Test;
import backend.ServerDatabaseConnection;

public class DatabaseTester {
	ServerDatabaseConnection sdc;
	
	public void init(){
		sdc = new ServerDatabaseConnection();
	}
	@Test
	public void test() {
		System.out.println(sdc.testConnection());
		sdc.insert("Lectures", new String[] {"1", "2", "testName"});
	}
}
