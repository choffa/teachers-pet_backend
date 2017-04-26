package tests;

import org.junit.Test;

import backend.ServerMain;

public class ServerMainTest {
	
	@Test
	public void testMain(){
		ServerMain sm = new ServerMain();
		sm.main(null);
	}
	
}
