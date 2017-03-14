package backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerConnection implements Runnable {

	private Socket client;
	private Scanner in;
	private PrintWriter out;
	private ServerDatabaseConnection sdc;
	
	public ServerConnection(Socket client){
		this.client = client;
		try {
			this.in = new Scanner(client.getInputStream());
			this.out = new PrintWriter(client.getOutputStream());
			sdc = new ServerDatabaseConnection();
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
					String ssrSuID = in.next();
					String ssrSID = in.next();
					String ssrRat = in.next();
					String ssrComment = "''";
					if(in.hasNext()) ssrComment = "'"+in.next()+"'";
					sdc.insert(ServerDatabaseConnection.SUBJECTRANKING, new String[] {ssrSuID,ssrRat,ssrComment,ssrSID});
					break;
				case "GET_AVERAGESUBJECTRATING":

					String table = "subject";
					String column = "rating";
					int id = in.nextInt();

					String gasrSuID = in.next();
					String[] returnList = sdc.getList(ServerDatabaseConnection.SUBJECTRANKING, "'Ranking'",gasrSuID, "'StudentID'");
					int avg = 0;
					for (String s:returnList) avg+=Integer.parseInt(s);
					out.println(avg);

					break;
				case "SET_SUBJECT":
					String ssSuID = in.next();
					//sdc.getInt(command, gasrSuID);
					break;
				case "GET_LECTURE":
					break;
				case "GET_ALLLECTURES":
					String galLID = in.next();
					String[] galreturnList = sdc.getList(ServerDatabaseConnection.LECTURES, "*","'DATE'", "NOW()");
					int galavg = 0;
					for (String s:returnList) out.println(avg);
					break;
				case "SET_LECTURE":
					break;
				case "SET_SPEEDRATING":
					String ssprLID = in.next();
					String ssprSID = in.next();
					String ssprRat = in.next();
					sdc.insert(ServerDatabaseConnection.SPEEDRANKING, new String[] {ssprLID, ssprRat,ssprSID});
					break;
				case "GET_AVERAGESPEEDRATING":
					String gasprLID = in.next();
					String[] gasprreturnList = sdc.getList(ServerDatabaseConnection.SPEEDRANKING, "'Ranking'",gasprLID, "'LectureID'");
					int gaspravg = 0;
					for (String s:returnList) gaspravg+=Integer.parseInt(s);
					out.println(avg);
					break;
				default:
					break;
			}
		}
	}

	private void getAverageSubjectRating(){
		String table = "SubjectRanking";
		String idColumn = "SubjectID";
		int id = in.nextInt();
		double avg = sdc.getAverage(table, idColumn, id);
		out.println(avg);
	}

	private void getAverageSpeedRating(){
		String table = "SpeedRanking";
		String idColumn = "LectureID";
		int id = in.nextInt();
		double avg = sdc.getAverage(table, idColumn, id);
		out.println(avg);
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
