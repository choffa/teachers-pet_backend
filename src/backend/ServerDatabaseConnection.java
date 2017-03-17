package backend;


import java.sql.*;

import java.util.ArrayList;


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
	public static final String USERS = "Users(UserName, PasswordHash, Salt)";
	
	
	
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
		String query = "SELECT AVG(ranking) FROM "+table+" WHERE " +idColumn+"="+id;
		double res = 0.0;
		try {
			connect();
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery(query);
			if (r.next()){
				res = r.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close();
			return res;
		}
	}
	public int getInt(String from, String what, String condition1, String condition2){
		return 0;
	}
	
	public String getString(String from, String what, String Condition1,String Condition2){
		return null;
	}
	
	
	/**
	 * Sends a query which returns a list.
	 * @param table
	 * @param condition1
	 * @param condition2
	 * @param what
	 * @return
	 */
	public String[] getList(String table, String condition1, String condition2, String... what){
		try {
			Statement s = con.createStatement();
			String what2="";
			for(String w:what) what2+=" "+w;
			String query = "SELECT"+what2+" FROM "+table.split("(")[0]+"WHERE "+condition1+"="+condition2+";";
			ResultSet rs = s.executeQuery(query);
			int numCol = rs.getMetaData().getColumnCount();
			ArrayList<String> list = new ArrayList<String>();
			while(rs.next()) {
				for(int i=1;i<=numCol;i++){
					list.add(rs.getString(i));
				}
				list.add("NEXT");
			}
			list.remove(list.size()-1);
			list.add("END");
			return (String[]) list.toArray();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public boolean checkUsername(String username) {
		try {
			Statement s = con.createStatement();
			String query = "SELECT username FROM Users WHERE username=" + username;
			ResultSet rs = s.executeQuery(query);
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
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
