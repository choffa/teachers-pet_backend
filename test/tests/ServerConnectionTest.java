package tests;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mysql.jdbc.Driver;


import com.sun.jmx.snmp.internal.SnmpAccessControlModel;

import backend.ServerConnection;
import backend.ServerDatabaseConnection;


import static org.junit.Test.*;

import java.io.*;
import java.net.Socket;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Random;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.Before.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import static org.mockito.AdditionalAnswers.returnsFirstArg;

import org.mindrot.jbcrypt.*;


public class ServerConnectionTest {


    private static ServerConnection sc;
    private static ServerDatabaseConnection sdc;

    private Socket skt;
    private PipedOutputStream printOut;
    private PipedInputStream in;
    private PipedOutputStream out;
    private PipedInputStream programReturn;
    private PrintWriter p;
    private Scanner s;
    private static Statement state;
    private static  Connection con;
    private static String database="jdbc:mysql://localhost/test_teacherspet";

    
    @Before
    public void init() throws IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
    	//clearing testdatabase
    	clearSchema();
    	
    	//piping I/O
    	printOut = new PipedOutputStream();
    	in = new PipedInputStream(printOut);
    	p = new PrintWriter(printOut);
    	out = new PipedOutputStream();
    	programReturn = new PipedInputStream(out);
    	s = new Scanner(programReturn);
    	
    	serverSetup();
    }
    
    
    private void serverSetup() throws IOException {

    	skt = mock(Socket.class);    	
    	when(skt.getOutputStream()).thenReturn(out);
    	when(skt.getInputStream()).thenReturn(in);
    	when(skt.isConnected()).thenReturn(true);

    	//initializing classes
    	sc = new ServerConnection(skt, sdc);
    	sdc = new ServerDatabaseConnection(database, "root", "");
	}




	private void clearSchema() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    	state.execute("DELETE FROM Lectures WHERE 1=1");
    	state.execute("DELETE FROM Subjects WHERE 1=1");
    	state.execute("DELETE FROM Users WHERE 1=1");
    	state.execute("DELETE FROM SpeedRanking WHERE 1=1");
    	state.execute("DELETE FROM SubjectRanking WHERE 1=1");
	}

    



    @BeforeClass
	public static void connect(){
		try{
    	Class.forName("com.mysql.jdbc.Driver").newInstance();
    	con = DriverManager.getConnection(database, "root", "");
    	state = con.createStatement();
		}catch(Exception e){e.printStackTrace();}
	}

    @AfterClass
    public static void closes() throws SQLException{
    	state.close();
    	con.close();
    }


	@Test
    public void setUser() throws NoSuchAlgorithmException, SQLException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    	String usr = md5("Harald");
    	String pwd = "rex";
    	System.out.println("Adding Harald");
		p.println("SET_USER "+usr+" "+SHA1(pwd));
		p.flush();
		new Thread(sc).start();
		Thread.sleep(500);
    	ResultSet rs = state.executeQuery("SELECT Username FROM Users WHERE Username='"+usr+"'");
    	rs.next();
    	assertEquals(usr, rs.getString(1));
    }


	@Test(timeout=2000)
	public void checkUser() throws NumberFormatException, IOException, NoSuchAlgorithmException, SQLException{
				insertThomas();
				p.println("CHECK_USER "+md5("Thomas"));

    			p.flush();
    			new Thread(sc).start();
    	    	assertTrue(s.nextBoolean());
    }


	
    // public void checkIfUpdate() {	} ikke testbar
    
    
    @Test
    public void validate() throws SQLException, InterruptedException, NoSuchAlgorithmException, IOException{
    	insertThomas();
		p.println("VALIDATE "+md5("Thomas")+" "+SHA1("123"));
    	p.flush();
    	new Thread(sc).start();
		Thread.sleep(500);
		assertTrue(s.nextBoolean());
    }

    
    @Test
    public void getSubjects() throws SQLException{
    	try{
    	String lec = insertLecture(insertThomas());
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName) VALUES ('"+lec+"','name1')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName) VALUES ('"+lec+"','name2')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName) VALUES ('"+lec+"','name3')");
    	p.println("GET_SUBJECTS "+lec);
    	p.flush();
    	new Thread(sc).start();
    	while("NEXT".equals(s.next())){
    		String Subject[][];
    	}
    	}catch (Exception e){e.printStackTrace();}
    }

    public void setSubjectRating() throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException, InterruptedException{
    	String subID = insertSubject("helloworld",insertLecture(insertThomas()));
    	String stud = insertHarald();
    	p.println("SET_SUBJECTRATING "+subID+" "+stud+" "+"3"+"NOCOMMENTS");
    	p.flush();
    	new Thread(sc).start();
    	Thread.sleep(100);
    	ResultSet rs = state.executeQuery("SELECT Ranking FROM SubjectRanking WHERE SubjectID='"+subID+"' AND StudentID='"+stud+"';");
		rs.next();
    	assertEquals("3", rs.getString(1));
    }

    @Test
    public void getAverageSubjectRating() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, InterruptedException{
    	String subID = insertSubject(insertLecture(insertThomas()),"helloworld");
    	String stud = insertHarald();
    	state.execute("INSERT INTO SubjectRanking(Ranking,RankingComment,SubjectID,StudentID) VALUES ('1','hei','"+subID+"','"+stud+");");
    	p.println("GET_AVERAGESUBJECTRATING "+subID);
    	p.flush();
    	new Thread(sc).start();
    	Thread.sleep(100);
    	String avg = s.nextLine();
    	assertEquals("1.0",avg);
    }

    @Test(timeout=2000)
    public void setSubject() throws SQLException, InterruptedException, NoSuchAlgorithmException, UnsupportedEncodingException{

		String prof = insertThomas();
		String lec = insertLecture(prof);
		p.println("SET_SUBJECT "+lec+" halla hei");
		p.flush();
		new Thread(sc).start();
		Thread.sleep(100);
		connect();
		ResultSet rs = state.executeQuery("SELECT SubjectName FROM Subjects WHERE SubjectName='halla'");
		rs.next();
    	assertEquals("halla", rs.getString(1));
    }

    
    //ok
	@Test
    public void getLecture() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, InterruptedException{
    	String prof = insertThomas();
    	String lec = insertLecture(prof);
    	p.println("GET_LECTURE "+prof);
    	p.flush();
    	new Thread(sc).start();
    	Thread.sleep(100);
    	ResultSet rs = state.executeQuery("SELECT * FROM Lectures WHERE Professor = "+prof);
    	rs.next();
    	assertTrue(rs.getString(1).length()>0);
    }

	//ok
    @Test
    public void setLecture() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, InterruptedException{
    		String prof = insertThomas();
    		String CID = "TDT4145";
    		String start = "12";
    		String end = "13";
    		String date = "2017-03-04";
    		String room = "R1";
    		p.println("SET_LECTURE "+prof+" "+CID+" "+date+" "+start+" "+end+" "+room);
    		p.flush();
    		new Thread(sc).start();
    		Thread.sleep(100);
    		String lec = s.nextLine();
    		ResultSet rs = state.executeQuery("SELECT Professor, StartTime, EndTime, LectureDate FROM Lectures WHERE LectureID="+lec);
    		rs.next();
        	assertEquals(prof, rs.getString(1));
        	assertEquals(start,rs.getString(2));
        	assertEquals(end,rs.getString(3));
        	assertEquals(date,rs.getString(4));
    }

    @Test
    public void setSpeedRating() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException, InterruptedException{
    		String prof = insertThomas();
    		String lec = insertLecture(prof);
    		p.println("SET_SUBJECT "+lec+" halla hei");
    		p.flush();
    		new Thread(sc).start();
    		Thread.sleep(100);
    		connect();
    		ResultSet rs = state.executeQuery("SELECT SubjectName FROM Subjects WHERE SubjectName='halla'");
    		rs.next();
        	assertEquals("halla", rs.getString(1));    		
    }

    //ok
    @Test
    public void getAverageSpeedRating() throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException, InterruptedException{
    	String lec = insertLecture(insertThomas());
    	String stud = insertHarald();
    	state.execute("INSERT INTO SpeedRanking(LectureID,Ranking,StudentID) VALUES ('"+lec+"','2','"+stud+"')");
    	p.println("GET_AVERAGESPEEDRATING "+lec);
    	p.flush();
    	new Thread(sc).start();
    	Thread.sleep(100);
    	String rank = s.nextLine();
    	assertEquals("2.0",rank);
    }




    @Test
    public void getTempoVotesInLecture() throws NoSuchAlgorithmException, UnsupportedEncodingException, SQLException, InterruptedException{
    	String lec = insertLecture(insertThomas());
    	String stud = insertHarald();
    	state.execute("INSERT INTO SpeedRanking(LectureID,Ranking,StudentID) VALUES ('"+lec+"','2','"+stud+"')");
    	p.println("GET_NUMBEROFUSERS "+lec);
    	p.flush();
    	new Thread(sc).start();
    	Thread.sleep(300);
    	String num = s.next();
    	assertEquals(1,Integer.parseInt(num));
    	
    }


    @Test
    public void updateSubject() throws SQLException, InterruptedException, NoSuchAlgorithmException, UnsupportedEncodingException{
		String prof = insertThomas();
		String lec = insertLecture(prof);
		String subID = insertSubject("sub1",lec);
		ResultSet rs = state.executeQuery("SELECT SubjectName FROM Subjects WHERE SubjectID='"+subID+"';");
		rs.next();
    	assertEquals("sub1", rs.getString(1));
		p.println("UPDATE_SUBJECT "+subID+" "+"name2"+" "+"comment");
		p.flush();
		new Thread(sc).start();
		Thread.sleep(300);
		rs = state.executeQuery("SELECT SubjectName FROM Subjects WHERE SubjectID='"+subID+"';");
		rs.next();
    	assertEquals("name2", rs.getString(1));
    }


	//------------------------------------Support methodes----------------
    
    private String insertThomas() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
		String usr = md5("Thomas");
		String salt = BCrypt.gensalt();
		String pwd = BCrypt.hashpw(SHA1("123"), salt);
		state.execute("INSERT INTO Users(UserName, PasswordHash, Salt) VALUES('"+usr+"','"+pwd+"','"+salt+"')");
		ResultSet rs = state.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		System.out.println("Thomases ID = "+rs.getString(1));
		return rs.getString(1);
	}

    private String insertHarald() throws SQLException, NoSuchAlgorithmException, UnsupportedEncodingException{
		String usr = md5("Harald");
		String salt = BCrypt.gensalt();
		String pwd = BCrypt.hashpw(SHA1("123"), salt);
		state.execute("INSERT INTO Users(UserName, PasswordHash, Salt) VALUES('"+usr+"','"+pwd+"','"+salt+"')");
		ResultSet rs = state.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		System.out.println("Haralds ID = "+rs.getString(1));
		return rs.getString(1);
	}
    
    private String insertLecture(String prof) throws SQLException {
		state.execute("INSERT INTO Lectures(LectureDate,StartTime,EndTime,Professor,Room,CourseID) VALUES('1995-03-02','14','15','"+prof+"','R1','TDT4145')");
		ResultSet rs = state.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		return rs.getString(1);
	}

    
    
    protected String md5(String id) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md=MessageDigest.getInstance("MD5");
        byte[] idBytes=id.getBytes("iso-8859-1");
        md.update(idBytes,0,idBytes.length);
        byte[] md5Hash=md.digest();
        return convertToHex(md5Hash);
    }
    
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }
    
    protected static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] textBytes = text.getBytes("iso-8859-1");
        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private String insertSubject(String name, String lecture) throws SQLException {
		state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lecture+"','"+name+"','comment')");
		ResultSet rs = state.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		return rs.getString(1);
	}
    
}
