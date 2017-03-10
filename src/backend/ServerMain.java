package backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

	public static void main(String[] args) {
		
	}
	
	private final int PORT = 4728;
	private ServerSocket ss;
	private Socket client;
	
	protected boolean init(){
		try {
			this.ss = new ServerSocket(PORT);
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	protected void run(){
		while (true){
			try {
				client = ss.accept();
				new Thread(new ServerConnection(client)).start();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
