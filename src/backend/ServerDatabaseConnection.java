package backend;

import java.sql.DriverManager;
import java.sql.SQLException;

import java.sql.Statement;

import java.sql.Connection;


public class ServerDatabaseConnection {
	private Connection con  =  null;
	private String url = "jdbc:mysql://mysql.idi.ntnu.no/p_teacherspet";
	private String user = "tpbackend_adm";
	private String pw = "ang4OhmieD7aefo";
	
	public static final String LECTURES = "Lectures(LectureID,LectureDate,StartTime,EndTime,Professor)";
	public static final String SUBJECTS = "Subjects(LectureID,SubjectID,SubjectName)";
	public static final String SUBJECTRANKING = "SubjectRanking(Ranking,RankingComment,SubjectID,StudentID)";
	public static final String SPEEDRANKING = "SpeedRanking(LectureID,Ranking,StudentID)";
	
	
	public void insert(String tableName, String[] args){
		try{
			connect();
			Statement s = con.createStatement();
			String values = "(";
			for (String arg:args) values+=arg+",";
			values = values.substring(0, values.length()-1);
			String query = "INSERT INTO "+tableName+" VALUES "+values+");";
			System.out.println(query);
			s.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("insert command failed");
			e.printStackTrace();
		}finally{
			close();
		}
	}
	
	
	
	
	public boolean testConnection(){
			connect();
			close();
		return true;
	}
	
	private void connect(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url,user,pw);
			System.out.println("Tilkoblingen fungerte.");
			  } catch (SQLException ex) {
			    System.out.println("Tilkobling feilet: "+ex.getMessage());
			  } catch (ClassNotFoundException ex) {
			    System.out.println("Feilet under driverlasting: "+ex.getMessage());
			  } catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  } catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			  } 
	}
	
	private void close(){
		{
		    try {
		      if (con !=  null) con.close();
		    } catch (SQLException ex) {
		      System.out.println("Epic fail: "+ex.getMessage());
		    }
		  }
	}
	
	public static void main(String[] args) {
		ServerDatabaseConnection sdc = new ServerDatabaseConnection();
		sdc.testConnection();
		sdc.insert(ServerDatabaseConnection.SUBJECTS, new String[] {"1", "2", "'testName'"});
	}

}
