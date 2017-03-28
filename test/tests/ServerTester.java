package tests;
import backend.*;
import frontend.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

public class ServerTester {
	private ServerDatabaseConnection sdc;
	private ServerConnection sc;
	private Connection c;
	private ServerMain sm;
	private Socket client;
	private ServerSocket ss;
	
	public void init(){
		sdc = new ServerDatabaseConnection();
		try {
			ss = new ServerSocket(4728);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
