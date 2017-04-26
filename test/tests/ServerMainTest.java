package tests;

import org.junit.Test;

import backend.ServerMain;

public class ServerMainTest {
	
	@Test
	public void testMain() throws InterruptedException{
		ServerMain sm = new ServerMain();
		sm.main(null);
		Thread.sleep(1000);
		sm = null;
		Runtime.getRuntime().gc();
	}
	
}
