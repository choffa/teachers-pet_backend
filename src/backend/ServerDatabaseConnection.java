package backend;


import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;



public class ServerDatabaseConnection {
	private Connection con  =  null;
	private String url = "jdbc:mysql://mysql.idi.ntnu.no/p_teacherspet"; 
	private String user = "tpbackend_adm";
	private String pw = "ang4OhmieD7aefo";
	
	
	/**
	 * Made so that other classes can add to the database without changes in the database affecting other classes than this class.
	 */
	public static final String LECTURES = "Lectures(LectureID,LectureDate,StartTime,EndTime,Professor)";
	public static final String SUBJECTS = "Subjects(LectureID,SubjectID,SubjectName)";
	public static final String SUBJECTRANKING = "SubjectRanking(Ranking,RankingComment,SubjectID,StudentID)";
	public static final String SPEEDRANKING = "SpeedRanking(LectureID,Ranking,StudentID)";
	
	
	
	/**
	 * Generic insert method.
	 * @param tableName
	 * @param args
	 */
	public void insert(String tableName, String[] args){
		try{
			connect();
			Statement s = con.createStatement();
			//Making insert query
			String values = "(";
			for (String arg:args) values+=arg+",";
			values = values.substring(0, values.length()-1);
			String query = "INSERT INTO "+tableName+" VALUES "+values+");";
			
			s.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("insert command failed");
			e.printStackTrace();
		}finally{
			close();
		}
	}
	

	public double getAverage(String table, String idColumn, int id){
		String query = "SELECT AVG(ranking) FROM "+table+" WHERE "+idColumn+"=" + id;
		try {
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery(query);
			if (r.next()){
				return r.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
}
	public int getInt(String from, String what, String condition){
		return 0;
	}
	
	public String getString(String from, String what, String Condition){
		return null;
	}
	
	public String[] getList(String table, String what, String condition1, String condition2){
		Statement s = con.createStatement()
		String query = "SELECT "+what+" FROM "+table.split("(")[0]+"WHERE "+condition1+"="+condition2+";";
		ResultSet rs = s.executeQuery(query);
		ArrayList<String> list = new ArrayList<String>();
		
		if (rs.getString(what)!=null) list.add(rs.getString(what));
		while(rs.next()) list.add(rs.getString(what));
		
		
		return (String[]) list.toArray();

	}
	
	/**
	 * Used by testclass to test connection. 
	 * @return true if method finishes (connection opens and closes)
	 */
	public boolean testConnection(){
			connect();
			close();
		return true;
	}
	
	
	/**
	 * Connects to database
	 */
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
	
	
	/**
	 * Closes database connection
	 */
	private void close(){
		{
		    try {
		      if (con !=  null) con.close();
		    } catch (SQLException ex) {
		      System.out.println("Epic fail: "+ex.getMessage());
		    }
		  }
	}
	
	
	/**
	 * testing method.
	 */
	public static void main(String[] args) {
		ServerDatabaseConnection sdc = new ServerDatabaseConnection();
		sdc.testConnection();
		sdc.insert(ServerDatabaseConnection.SUBJECTS, new String[] {"1", "2", "'testName'"});
	}

}
