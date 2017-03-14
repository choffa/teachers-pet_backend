package backend;

import java.sql.*;


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
	
	public double getAverage(String table, String column, int id){
		String query = "SELECT AVG("+column+") FROM "+table+" WHERE " + table + "id";
		Statement s;
		try {
			s = con.createStatement();
			ResultSet r = s.executeQuery(query);
			if (r.next()){
				return r.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0.0;
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
