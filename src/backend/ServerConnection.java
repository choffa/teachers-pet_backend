package backend;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import org.mindrot.jbcrypt.BCrypt;

public class ServerConnection implements Runnable {

	private Socket client;
	private Scanner in;
	private PrintWriter out;
	private ServerDatabaseConnection sdc;
	
	public ServerConnection(Socket client, ServerDatabaseConnection sdc){
		this.client = client;
		try {
			this.in = new Scanner(client.getInputStream());
			this.out = new PrintWriter(client.getOutputStream());
			this.sdc = sdc;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ServerConnection (Socket client) {
		this(client, new ServerDatabaseConnection());
	}
	
	@Override
	public void run() {
		while (true){
			System.out.println("You're a good lad");
			if(in.hasNext()){
				System.out.println("command recieved");
				String command = in.next();
				System.out.println(command);
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
						System.out.println("entered setLecture case");
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
					case "GET_NUMBEROFUSERS":
						getTempoVotesInLecture();
						break;
					case "UPDATE_SUBJECT":
						updateSubject();
						break;
					case "GET_STATS":
						getStats();
						break;
					case "GET_STUDENTSUBJECTRATING":
						getStudSubRating();
						break;
					case "GET_STUDENTSPEEDRATING":
						getStudSpeedRating();
						break;
					case "SET_LECTURECOMMENT":
						setLectureComment();
						break;
					case "GET_LECTURECOMMENTS":
						getLectureComments();
						break;
					default:
						close();
						return;
				}
			} else {
				close();
				return;
			}
		}
	}


	private void getLectureComments() {
		String LID = in.next();
		String[] comments = sdc.getList(ServerDatabaseConnection.LECTURECOMMENTS, "LectureID", LID, new String[]{"Comment"});
		String returnString = "";
		for (String comment:comments) {
			returnString+= comment+" ";
		}
		out.println(returnString);
		out.flush();
	}


	private void getStudSpeedRating() {
		String SID = "'"+in.next()+"'";
		String LID = "'"+in.next()+"'";
		String rating = sdc.getString(ServerDatabaseConnection.SPEEDRANKING, "Ranking", "StudentID", SID, "LectureID", LID);
		if(rating.length()>0){
			out.println(rating);
		} else {
			out.println("NOSTRING");
		}
		out.flush();
	}

	private void getStudSubRating() {
		String SID = "'"+in.next()+"'";
		String SuID = "'"+in.next()+"'";
		String rating = sdc.getString(ServerDatabaseConnection.SUBJECTRANKING, "Ranking", "StudentID", SID, "SubjectID", SuID);
		if(rating.length()>0){
			out.println(rating);
		} else {
			out.println("NOSTRING");
		}
		out.flush();
	}

	private void checkUser() {
		boolean outBool = sdc.checkUsername(in.next());
		out.println(outBool);
		out.flush();
	}
	

	private void validate() {
		String username = in.next();
		String password = in.next();
		String hash = sdc.getHash(username);
		boolean res = BCrypt.checkpw(password, hash);
		out.println(res);
		out.flush();
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
		String ssprRat = in.next();
		String ssprSID = in.next();
		if(checkIfUpdate(ServerDatabaseConnection.SPEEDRANKING,new String[] {"StudentID", "LectureID"},new String[] {ssprSID,ssprLID})){
			sdc.update(ServerDatabaseConnection.SPEEDRANKING, new String[] {"Ranking"}, new String[] {ssprRat}, "StudentID", "'"+ssprSID+"'", "LectureID", "'"+ssprLID+"'");
			System.out.println("Rating updated");
		} else {
			sdc.insert(ServerDatabaseConnection.SPEEDRANKING, new String[] {ssprLID, ssprRat,ssprSID});
			System.out.println("New rating inserted");
		}
		
	}

	private boolean checkIfUpdate(String table, String[] what, String[] values) {
		String s;
 		s=sdc.getString(table, "*", what[0], "'"+values[0]+"'", what[1], "'"+values[1]+"'");
		return s.length()>0;
	}

	private void setLecture() {
		String PID= in.next();
		String CID= in.next();
		String date= in.next();
		String start= in.next();
		String end= in.next();
		String room= in.next();
		sdc.insert(ServerDatabaseConnection.LECTURES, new String[] {date, start, end, PID, room, CID});
		int ID=-1;
		try{
			ID = Integer.parseInt(sdc.getLastID());
		} finally {
		System.out.println("Created Lecture ID: "+ID);
		out.println(ID);
		out.flush();
		}
	}
	
	private void setLectureComment() {
		String LID = in.next();
		String comment = in.next();
		sdc.insert(ServerDatabaseConnection.LECTURECOMMENTS, new String[]{LID, comment});
	}
	
	private void setSubject(){
		String table = ServerDatabaseConnection.SUBJECTS;
		int lectureID = in.nextInt();
		String name = in.next();
		String comment = in.next();
		String[] args = {Integer.toString(lectureID), name, comment};
		sdc.insert(table, args);
		System.out.println("Subject inserted");
	}
	
	private void updateSubject(){
		String subID = in.next();
		String subName = in.next();
		String subComment = in.next();
		sdc.update(ServerDatabaseConnection.SUBJECTS, new String[]{"SubjectName", "Comment"}, new String[]{subName,subComment}, "SubjectID", "'"+subID+"'", "'1'", "'1'");
	}

	private void setSubjectRating(){
		String ssrSuID = in.next();
		String ssrSID = in.next();
		String ssrRat = in.next();
		String ssrComment = in.next();
		if(checkIfUpdate(ServerDatabaseConnection.SUBJECTRANKING,new String[] {"StudentID", "SubjectID"},new String[] {ssrSID, ssrSuID})){
			sdc.update(ServerDatabaseConnection.SUBJECTRANKING, new String[] {"Ranking", "RankingComment"}, new String[] {ssrRat, ssrComment}, "StudentID", "'"+ssrSID+"'", "SubjectID", "'"+ssrSuID+"'");
			System.out.println("Rating updated");
		} else {
			sdc.insert(ServerDatabaseConnection.SUBJECTRANKING, new String[] {ssrRat,ssrComment,ssrSuID,ssrSID});
			System.out.println("New rating inserted");
		}
	}

	private void getAverageSubjectRating(){
		String table = "SubjectRanking";
		String idColumn = "SubjectID";
		String id = in.next();
		double avg = sdc.getAverage(table, idColumn, id);
		float ret = (float) avg;
		out.println(ret);
		out.flush();
	}

	private void getAverageSpeedRating(){
		String table = "SpeedRanking";
		String idColumn = "LectureID";
		String id = in.next();
		double avg = sdc.getAverage(table, idColumn, id);
		float ret = (float) avg;
		out.println(ret);
		out.flush();
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
		String[] galReturnList = sdc.getList(ServerDatabaseConnection.LECTURES,"LectureDate", "CURDATE()",new String[] {"*"});
		String galReturnString="";
		if(galReturnList.length>0)
		for (String s:galReturnList) galReturnString+=s+" ";
		out.println(galReturnString);
		out.flush();
	}
	
	private void getLecture(){
		String PID = in.next();
		String[] ReturnList = sdc.getList(ServerDatabaseConnection.LECTURES, "Professor", "'"+PID+"'",new String[] {"*"});
		String ReturnString="";
		for (String s:ReturnList) ReturnString+=s+" ";
		out.println(ReturnString);
		out.flush();
	}
	
	private void getSubjects(){
		String LID = in.next();
		String[] ReturnList = sdc.getList(ServerDatabaseConnection.SUBJECTS,"LectureID", "'"+LID+"'", new String[] {"SubjectID","SubjectName", "Comment"});
		String ReturnString="";
		if(ReturnList.length>0)
		for (String s:ReturnList) ReturnString+=s+" ";
		out.println(ReturnString);
		out.flush();
	}
	

	private void getTempoVotesInLecture() {
		String LID = in.next();
		int numOfUsers = sdc.getInt(ServerDatabaseConnection.SPEEDRANKING, "COUNT(*)", "LectureID", "'"+LID+"'");
		out.println(numOfUsers);
		out.flush();
	}
	
	 private void getStats(){
		 String SuID = in.next();
		 String stats = sdc.getStats(SuID);
		 //System.out.println("Server returning: "+stats);
		 out.println(stats);
		 out.flush();
	 }
}
