package backend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

	public static void main(String[] args) {
		ServerMain main = new ServerMain();
		main.init();
		System.out.println("Server starting");
		main.run();
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
		System.out.println("Server started");
		while (true){
			try {
				client = ss.accept();
				System.out.println("Connection accepted");
				new Thread(new ServerConnection(client)).start();
				client = null;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
		}
	}

}
