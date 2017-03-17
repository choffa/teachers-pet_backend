package backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

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
					setSubjectRating();
					break;
				case "GET_AVERAGESUBJECTRATING":
					getAverageSubjectRating();
					break;
				case "SET_SUBJECT":
					setSubject();
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
					setSpeedRating();
					break;
				case "GET_AVERAGESPEEDRATING":
					getAverageSpeedRating();
					break;
				case "SET_USER":
					setUser();
					break;
				case "CHECK_USER":
					checkUser();
					break;
				case "VALIDATE":
					validate();
					break;
				default:
					close();
					return;
			}
		}
	}

	private void checkUser() {

	}

	private void validate() {

	}

	private void setUser() {
		String userName = in.next();
		String password = in.next();
		String salt = BCrypt.gensalt();
		String hash = BCrypt.hashpw(password, salt);
		sdc.insert(ServerDatabaseConnection.USERS, new String[] {userName, hash, salt});
	}


	private void setSpeedRating(){
		String ssprLID = in.next();
		String ssprSID = in.next();
		String ssprRat = in.next();
		sdc.insert(ServerDatabaseConnection.SPEEDRANKING, new String[] {ssprLID, ssprRat,ssprSID});
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
	private void setSubject(){
		String table = "subjects";
		int lectureID = in.nextInt();
		String name = "'"+in.next()+"'";
		String[] args = {Integer.toString(lectureID), name};
		sdc.insert(table, args);
	}

	private void setSubjectRating(){
		String ssrSuID = in.next();
		String ssrSID = in.next();
		String ssrRat = in.next();
		String ssrComment = "'"+in.next()+"'";
		sdc.insert(ServerDatabaseConnection.SUBJECTRANKING, new String[] {ssrSuID,ssrRat,ssrComment,ssrSID});

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
