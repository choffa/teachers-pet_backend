package backend;


import java.sql.*;

import java.util.ArrayList;


public class ServerDatabaseConnection {
	private Connection con  =  null;
	private static final String url = "jdbc:mysql://mysql.stud.ntnu.no/mathilie_teacherspet";
	private static final String user = "mathilie_pu";
	private static final String pw = "pu123";
	
	
	/**
	 * Made so that other classes can add to the database without changes in the database affecting other classes than this class.
	 */
	public static final String LECTURES = "Lectures(LectureDate,StartTime,EndTime,Professor,Room,CourseID)";
	public static final String SUBJECTS = "Subjects(LectureID,SubjectName,Comment)";
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
			Statement s = con.createStatement();
			//Making insert query
			String values = "(";
			for (String arg:args) values+="'"+arg+"'"+",";
			values = values.substring(0, values.length()-1);
			String query = "INSERT INTO "+tableName+" VALUES "+values+");";
			
			s.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("insert command failed");
			e.printStackTrace();
		}
	}
	
	public void update(String tableName, String[] colNames, String[] values, String condition1,String condition2,/*Shortcut for updateSpeedRanking*/  String condition3, String condition4) {
		try{
			Statement s = con.createStatement();
			//Making insert query
			String set = "";
			for (int i=0;i<colNames.length;i++) set+=colNames[i]+"="+"'"+values[i]+"',";
			set = set.substring(0, set.length()-1);
			String query = "UPDATE "+tableName.split("\\(")[0]+" SET "+set+" WHERE "+condition1+"="+condition2+/*Shortcut*/" AND "+condition3+"="+condition4+";";
			
			s.execute(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("update command failed");
			e.printStackTrace();
		}
	}
	

	public double getAverage(String table, String idColumn, String id){
		String query = "SELECT AVG(Ranking) FROM "+table+" WHERE " +idColumn+"="+"'"+id+"';";
		double res = 0.0;
		try {
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery(query);
			if (r.next()){
				res = r.getDouble(1);
			}
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return res;
		} 
	}
	
	public int getInt(String from, String what, String condition1, String condition2){
		String query = "SELECT "+what+" FROM "+from.split("\\(")[0]+" WHERE " +condition1+"="+condition2;
		int res = -1;
		try {
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery(query);
			if (r.next()){
				res = r.getInt(1);
			}
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return res;
		} 
	}
	
	public String getString(String from, String what, String condition1,String condition2,/*Shortcut for checkuserID*/  String condition3, String condition4){
		String query = "SELECT "+what+" FROM "+from.split("\\(")[0]+" WHERE " +condition1+"="+condition2+/*Shortcut*/" AND "+condition3+"="+condition4+";";
		String res = "";
		try {
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery(query);
			if (r.next()){
				res = r.getString(1);
			}
			return res;
		} catch (SQLException e) {
			e.printStackTrace();
			return res;
		}
	}
	
	
	/**
	 * Sends a query which returns a list.
	 * @param table
	 * @param condition1
	 * @param condition2
	 * @param what
	 * @return
	 */
	public String[] getList(String table, String condition1, String condition2, String[] what){
		try {
			Statement s = con.createStatement();
			String what2="";
			for(String w:what) what2+=" "+w+",";
			what2 = what2.substring(0, what2.length()-1);
			String query = "SELECT"+what2+" FROM "+table.split("\\(")[0]+" WHERE "+condition1+"="+condition2+";";
			ResultSet rs = s.executeQuery(query);
			int numCol = rs.getMetaData().getColumnCount();
			ArrayList<String> list = new ArrayList<String>();
			while(rs.next()) {
				list.add("NEXT");
				for(int i=1;i<=numCol;i++){
					list.add(rs.getString(i));
				}
			}
			list.add("END");
			String[] returnlist= list.toArray(new String[list.size()]);
			return returnlist;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}

	public boolean checkUsername(String username) {
		try {
			Statement s = con.createStatement();
			String query = "SELECT username FROM Users WHERE username="+"'"+username+"'";
			ResultSet rs = s.executeQuery(query);
			if(rs.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		} 
	}

	public String getHash(String username) {
		try {
			Statement s = con.createStatement();
			String query = "SELECT PasswordHash FROM Users WHERE Username="+"'"+username+"'";
			ResultSet rs = s.executeQuery(query);
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	
	public String getLastID(){
		try {
			Statement s = con.createStatement();
			String query = "SELECT last_insert_id()";
			ResultSet rs = s.executeQuery(query);
			if (rs.next()) {
				System.out.println("got "+rs.getString(1));
				return rs.getString(1);
			}
			System.out.println("RS had no next");
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	public String getStats(String SubjectID){
		int[] Return = {0,0,0,0,0,0};
		try {
			Statement s = con.createStatement();
			String query = "SELECT Ranking, COUNT(RANKING) FROM SubjectRanking WHERE Subject="+"'"+SubjectID+"'"+" GROUP BY Ranking";
			ResultSet rs = s.executeQuery(query);
			if (rs.next()) {
				int ranking = Integer.parseInt(rs.getString(1));
				int count = Integer.parseInt(rs.getString(2));
				Return[ranking] = count;
			}
			String returnString = "";
			for(short i = 0;i<5;i++){
				returnString+=Return[i];
				returnString+=(" NEXT ");
			}
			returnString+=Return[5];
			returnString+=" END";
			return returnString;
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * Used by testclass to test connection. 
	 * @return true if method finishes (connection opens and closes)
	 */
	public boolean testConnection(){
			close();
		return true;
	}
	
	
	/**
	 * Connects to database
	 */
	public ServerDatabaseConnection(String url, String user, String pw) {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url,user,pw);
			System.out.println("Database connected.");
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

	public ServerDatabaseConnection() {
		this(url, user, pw);
	}
	
	
	/**
	 * Closes database connection
	 */
	public void close(){
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
		//sdc.insert(ServerDatabaseConnection.SUBJECTS, new String[] {"1", "2", "'testName'"});
	}






}
