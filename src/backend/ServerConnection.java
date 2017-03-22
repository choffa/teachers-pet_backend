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
					default:
						close();
						return;
				}
			}
		}
	}


	private void checkUser() {
		System.out.println("Entered checkUser method");
		System.out.println("Socket has next: "+in.hasNext());
		boolean outBool = sdc.checkUsername(in.next());
		System.out.println("Returns boolean: "+outBool);
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
		if(checkIfUpdate(ssprSID, ssprLID)){
			sdc.update(ServerDatabaseConnection.SPEEDRANKING, new String[] {"Ranking"}, new String[] {ssprRat}, "StudentID", "'"+ssprSID+"'", "LectureID", "'"+ssprLID+"'");
		} else {
			sdc.insert(ServerDatabaseConnection.SPEEDRANKING, new String[] {ssprLID, ssprRat,ssprSID});
		}
		
	}

	private boolean checkIfUpdate(String SID, String LID) {
		String s;
 		s=sdc.getString(ServerDatabaseConnection.SPEEDRANKING, "StudentID", "StudentId", "'"+SID+"'", "LectureID", "'"+LID+"'");
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
		String[] ReturnList = sdc.getList(ServerDatabaseConnection.SUBJECTS,"'LectureID", "'"+LID+"'", new String[] {"*"});
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
}
