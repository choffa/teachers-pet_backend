package tests;

import backend.ServerConnection;
import backend.ServerDatabaseConnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import static org.mockito.Mockito.*;

/**
 * Created by Choffa on 22-Mar-17.
 */
public class ServerConnectionTest {

    public static String commands = "SET_USER test testpassword VALIDATE test testpassword CLOSE";

    public static void main(String[] args) throws IOException {
        String url = "jdbc:mysql://localhost:3306/teacherspet?useSSL=false";
        String user = "root";
        String pw = "root";
        ServerDatabaseConnection dbc = new ServerDatabaseConnection(url, user, pw);
        Socket s = mock(Socket.class);
        InputStream is = new ByteArrayInputStream(commands.getBytes("UTF-8"));
        when(s.getInputStream()).thenReturn(is);
        when(s.getOutputStream()).thenReturn(System.out);

        ServerConnection sc = new ServerConnection(s, dbc);
        sc.run();
    }
}
