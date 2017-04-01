package tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jmx.snmp.internal.SnmpAccessControlModel;

import backend.ServerConnection;
import backend.ServerDatabaseConnection;
import frontend.Connection;

import static org.junit.Test.*;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.Before.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mindrot.jbcrypt.BCrypt;


public class ServerConnectionTest {

    private String commands = "SET_USER test testpassword VALIDATE test testpassword CLOSE";
    private ServerConnection sc;
    private ServerDatabaseConnection sdc;
    private Socket skt;
    private PipedOutputStream printOut;
    private PipedInputStream in;
    private PipedOutputStream out;
    private PipedInputStream programReturn;
    private PrintWriter p;
    private Scanner s;
   
    
    
    @Before
    public void init() throws IOException {
    	printOut = new PipedOutputStream();
    	in = new PipedInputStream(printOut);
    	p = new PrintWriter(printOut);
    	
    	out = new PipedOutputStream();
    	programReturn = new PipedInputStream(out);
    	s = new Scanner(programReturn);

    	
 
    	skt = mock(Socket.class);    	
    	when(skt.getOutputStream()).thenReturn(out);
    	when(skt.getInputStream()).thenReturn(in);
    	when(skt.isConnected()).thenReturn(true);
    	sdc = mock(ServerDatabaseConnection.class);
    	sc = new ServerConnection(skt, sdc);
    	when(sdc.checkUsername(anyString())).thenReturn(true);
    }
    
 

    
    @Test(timeout=2000)
    public void testCheckUser() throws NumberFormatException, IOException{
    			p.println("CHECK_USER aljsd");
    			p.flush();
    			new Thread(sc).start();
    	    	assertTrue(s.nextBoolean());
    }
    
    @Test
    public void testValidate(){
    	
    }
    
    
}
