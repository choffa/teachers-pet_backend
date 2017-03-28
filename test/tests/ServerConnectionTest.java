package tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import backend.ServerConnection;
import backend.ServerDatabaseConnection;

import static org.junit.Test.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.net.Socket;
import java.util.stream.Stream;

import static org.junit.Before.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class ServerConnectionTest {

    private String commands = "SET_USER test testpassword VALIDATE test testpassword CLOSE";
    private ServerConnection sc;
    private ServerDatabaseConnection sdc;
    private Socket s;
    private OutputStream out;
    private InputStream in;
    private PrintWriter p;
    
    
    @Before
    public void init() throws IOException {
    	p = new PrintWriter(out);
    	s = mock(Socket.class);    	
    	when(s.getOutputStream()).thenReturn(out);
    	when(s.getInputStream()).thenReturn(in);
    	sdc = mock(ServerDatabaseConnection.class);
    	sc = new ServerConnection(s, sdc);
    }
    
    @Test
    public void closeTest() throws NumberFormatException, IOException{
    	out = new ByteArrayOutputStream();
    	out.write(Integer.valueOf("ClOSE"));
    	sc.run();
    	
    }
    
    
}
