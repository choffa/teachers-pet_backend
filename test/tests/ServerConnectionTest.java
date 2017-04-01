package tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.jmx.snmp.internal.SnmpAccessControlModel;

import backend.ServerConnection;
import backend.ServerDatabaseConnection;
import frontend.Connection;

import static org.junit.Test.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.Socket;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.Before.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class ServerConnectionTest {

    private String commands = "SET_USER test testpassword VALIDATE test testpassword CLOSE";
    private ServerConnection sc;
    private ServerDatabaseConnection sdc;
    private Socket s;
    private ByteArrayOutputStream out;
    private ByteArrayInputStream in;
    private PrintWriter p;
    private String serverReturn = "1.54 END END 4.355";
    
    
    @Before
    public void init() throws IOException {
    	s = mock(Socket.class);    	
    	when(s.getOutputStream()).thenReturn(out);
    	when(s.getInputStream()).thenReturn(in);
    	when(s.isConnected()).thenReturn(true);
    	sdc = mock(ServerDatabaseConnection.class);
    	sc = new ServerConnection(s, sdc);
    	when(sdc.testConnection()).thenReturn(true);
    	
    	byte[] b = serverReturn.getBytes();
    }
    
    public void test2(){
    	
    	
    	
    }

    
    @Test
    public void closeTest() throws NumberFormatException, IOException{
    	out = new ByteArrayOutputStream();
    	out.write(Integer.valueOf("ClOSE"));
    	sc.run();
    	
    }
    
    
}
