package tests;

import backend.ServerConnection;
import backend.ServerDatabaseConnection;

import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.*;

/**
 * Created by Choffa on 22-Mar-17.
 */
public class ServerConnectionTest {

    public static void main(String[] args) throws IOException {
        ServerDatabaseConnection dbcon = mock(ServerDatabaseConnection.class);
        Socket client = mock(Socket.class);
        when(client.getInputStream()).thenReturn(System.in);
        when(client.getOutputStream()).thenReturn(System.out);
        when(client.isConnected()).thenReturn(true);

        ServerConnection con = new ServerConnection(client, dbcon);

        when(dbcon.getInt(anyString(), anyString(), anyString(), anyString())).thenReturn(100);
        when(dbcon.getHash(anyString())).thenReturn("hdfksløafjdksløfjasklføsdjfkølsdjfkdlsø");
        when(dbcon.getAverage(anyString(), anyString(), anyInt())).thenReturn(4.55);

        con.run();
    }
}
