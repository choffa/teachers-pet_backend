package backend.sockets;

import java.io.IOException;
import java.net.ServerSocket;


/**
 * A class for simplifying the making of a serverSocket
 *
 * @author Choffa
 */
public class CustomServerSocket {

	final static int port = 4279;
	
	/**
	 * The method for returning a ServerSocket.
	 * @return A new ServerSocket on port 4279.
	 */
	public static ServerSocket getSocket() {
		try {
			return new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}
}
