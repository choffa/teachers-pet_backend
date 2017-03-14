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
					getSubjects();
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
					getAverageSubjectRating();
					break;
				case "SET_SUBJECT":
					String ssSuID = in.next();
					//sdc.getInt(command, gasrSuID);
					break;
				case "GET_LECTURE":
					getLecture();
					break;
				case "GET_ALLLECTURES":
					getAllLectures();
					break;
				case "SET_LECTURE":
					setLecture();
					break;
				case "SET_SPEEDRATING":
					String ssprLID = in.next();
					String ssprSID = in.next();
					String ssprRat = in.next();
					sdc.insert(ServerDatabaseConnection.SPEEDRANKING, new String[] {ssprLID, ssprRat,ssprSID});
					break;
				case "GET_AVERAGESPEEDRATING":
					getAverageSpeedRating();
					break;
				default:
					break;
			}
		}
	}

	private void setLecture() {
		String PID= in.next();
		String CID= in.next();
		String date= in.next();
		String start= in.next();
		String end= in.next();
		String room= in.next();
		
		/*out.println("SET_LECTURE " + professorID + " " + courseID + " " + date + " " + start + " "
				+ end + " " + room);*/
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
	
	
	private void getAllLectures(){
		String galLID = in.next();
		String[] galReturnList = sdc.getList(ServerDatabaseConnection.LECTURES, "*","'DATE'", "NOW()");
		String galReturnString="";
		for (String s:galReturnList) galReturnString+=s+" ";
		out.println(galReturnString);
	}
	
	private void getLecture(){
		String PID = in.next();
		String[] ReturnList = sdc.getList(ServerDatabaseConnection.LECTURES, "*","'ProfessorID", "'"+PID+"'");
		String ReturnString="";
		for (String s:ReturnList) ReturnString+=s+" ";
		out.println(ReturnString);
	}
	
	private void getSubjects(){
		String LID = in.next();
		String[] ReturnList = sdc.getList(ServerDatabaseConnection.SUBJECTS, "*","'LectureID", "'"+LID+"'");
		String ReturnString="";
		for (String s:ReturnList) ReturnString+=s+" ";
		out.println(ReturnString);
	}
}
