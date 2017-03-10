package backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerConnection implements Runnable {

	private Socket client;
	private Scanner in;
	private PrintWriter out;
	
	public ServerConnection(Socket client){
		this.client = client;
		try {
			this.in = new Scanner(client.getInputStream());
			this.out = new PrintWriter(client.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while (true){
			String command = in.next();
			switch (command){
				case "CLOSE":
					close();
					return;
				case "GET_SUBJECTS":
					break;
				case "SET_SUBJECTRATING":
					break;
				case "GET_AVERAGESUBJECTRATING":
					break;
				case "SET_SUBJECT":
					break;
				case "GET_LECTURE":
					break;
				case "GET_ALLLECTURES":
					break;
				case "SET_LECTURE":
					break;
				case "SET_SPEEDRATING":
					break;
				case "GET_AVERAGESPEEDRATING":
					break;
				default:
					break;
			}
		}
	}
	
	private void close(){
		out.flush();
		out.close();
		in.close();
		try {
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
