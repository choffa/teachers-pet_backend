package tests;

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

import org.mindrot.jbcrypt.BCrypt;


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
    	sdc = new ServerDatabaseConnection("jdbc:mysql://localhost/teachers-pet_test_database?autoReconnect=true&useSSL=false", "root", "123");
	}




	private void clearSchema() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
    	connect();
    	try{
    	state.execute("DELETE FROM lectures WHERE 1=1");
    	state.execute("DELETE FROM subjects WHERE 1=1");
    	state.execute("DELETE FROM users WHERE 1=1");
    	state.execute("DELETE FROM speedranking WHERE 1=1");
    	state.execute("DELETE FROM subjectranking WHERE 1=1");
    	}catch(Exception e)  {e.printStackTrace();}
    	close();
	}

    
    private void close() throws SQLException {
    	state.close();
    	con.close();
	}




	private void connect(){
		try{
    	Class.forName("com.mysql.jdbc.Driver").newInstance();
    	con = DriverManager.getConnection("jdbc:mysql://localhost/test_teacherspet?autoReconnect=true&useSSL=false", "root", "");
    	state = con.createStatement();
		}catch(Exception e){e.printStackTrace();}
	}




	@Test
    public void setUser() throws NoSuchAlgorithmException, SQLException, InterruptedException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    	String usr = md5("Harald");
    	String pwd = "rex";
		p.println("SET_USER "+usr+" "+SHA1(pwd));
		p.flush();
		new Thread(sc).start();
		Thread.sleep(500);
    	connect();
    	ResultSet rs = state.executeQuery("SELECT Username FROM users WHERE Username='"+usr+"'");
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
    public void setUserAndValidate() throws SQLException, InterruptedException, NoSuchAlgorithmException, IOException{
    	
    	String usr = md5("Harald");
    	String pwd = "rex";
		p.print("SET_USER "+usr+" "+SHA1(pwd));
		p.flush();
		new Thread(sc).start();
		Thread.sleep(500);
		p.print("VALIDATE "+usr+" "+SHA1(pwd));
    	p.flush();
		Thread.sleep(500);

    }

    
    @Test
    public void getSubjects() throws SQLException{
    	try{
    	String lec = insertLecture(insertThomas());
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name1','comment1')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name2','comment2')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name3','comment3')");
    	p.println("GET_SUBJECTS "+lec);
    	p.flush();
    	new Thread(sc).start();
    	while("NEXT".equals(s.next())){
    		String Subject[][];
    	}
    	}catch (Exception e){e.printStackTrace();}
    }

    public void setSubjectRating(){
    	
    }

    @Test
    public void getAverageSubjectRating(){
    	try{
    	String lec = insertLecture(insertThomas());
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name1','comment1')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name2','comment2')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name3','comment3')");
    	p.println("GET_SUBJECTS "+lec);
    	p.flush();
    	new Thread(sc).start();
    	while("NEXT".equals(s.next())){
    		String Subject[][];
    	}
    	}catch (Exception e){e.printStackTrace();}
    }

    @Test(timeout=2000)
    public void setSubject() throws SQLException{
		try{
		String prof = insertThomas();
		String lec = insertLecture(prof);
		p.println("SET_SUBJECT "+lec+" halla hei");
		p.flush();
		new Thread(sc).start();
		Thread.sleep(100);
		connect();
		ResultSet rs = state.executeQuery("SELECT SUbjectName FROM Subjects WHERE SubjectName='halla'");
		rs.next();
    	assertEquals("halla", rs.getString(1));
		} catch (Exception e) {e.printStackTrace();fail("stuff crashed");}
		finally{
			close();
		}
    }

	@Test
    public void getLecture(){
    	try{
    	String lec = insertLecture(insertThomas());
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name1','comment1')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name2','comment2')");
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name3','comment3')");
    	p.println("GET_SUBJECTS "+lec);
    	p.flush();
    	new Thread(sc).start();
    	while("NEXT".equals(s.next())){
    		String Subject[][];
    	}
    	}catch (Exception e){e.printStackTrace();}
    }

    @Test
    public void getAllLectures(){
    	try{
    	connect();
    	String lec = insertLecture(insertThomas());
    	state.execute("INSERT INTO  VALUES ('"+lec+"','name1','comment1')");
    	p.flush();
    	new Thread(sc).start();
    	while("NEXT".equals(s.next())){
    		String Subject[][];
    	}
    	}catch (Exception e){e.printStackTrace();}
    }

    @Test
    public void setLecture() throws SQLException{
    	try{
    		String prof = insertThomas();
    		String lec = insertLecture(prof);
    		p.println("SET_SUBJECT "+lec+" halla hei");
    		p.flush();
    		new Thread(sc).start();
    		Thread.sleep(100);
    		connect();
    		ResultSet rs = state.executeQuery("SELECT SUbjectName FROM Subjects WHERE SubjectName='halla'");
    		rs.next();
        	assertEquals("halla", rs.getString(1));
    		} catch (Exception e) {e.printStackTrace();fail("stuff crashed");}
    		finally{
    			close();
    		}
    }

    @Test
    public void setSpeedRating() throws SQLException{
    	try{
    		String prof = insertThomas();
    		String lec = insertLecture(prof);
    		p.println("SET_SUBJECT "+lec+" halla hei");
    		p.flush();
    		new Thread(sc).start();
    		Thread.sleep(100);
    		connect();
    		ResultSet rs = state.executeQuery("SELECT SUbjectName FROM Subjects WHERE SubjectName='halla'");
    		rs.next();
        	assertEquals("halla", rs.getString(1));
    		} catch (Exception e) {e.printStackTrace();fail("stuff crashed");}
    		finally{
    			close();
    		}
    }

    @Test
    public void getAverageSpeedRating(){
    	try{
    	String lec = insertLecture(insertThomas());
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name1','comment1')");
    	
    	p.println("GET_SUBJECTS "+lec);
    	p.flush();
    	new Thread(sc).start();
    	while("NEXT".equals(s.next())){
    		String Subject[][];
    	}
    	}catch (Exception e){e.printStackTrace();}
    }




    @Test
    public void getTempoVotesInLecture(){
    	try{
    	String lec = insertLecture(insertThomas());
    	state.execute("INSERT INTO Subjects(LectureID,SubjectName,Comment) VALUES ('"+lec+"','name1','comment1')");
    	
    	p.println("GET_SUBJECTS "+lec);
    	p.flush();
    	new Thread(sc).start();
    	while("NEXT".equals(s.next())){
    		String Subject[][];
    	}
    	}catch (Exception e){e.printStackTrace();}
    }


    @Test
    public void updateSubject(){
    	
    }
    
    
    private String insertThomas() throws SQLException{
		try{
    	connect();
		String usr = md5("Thomas");
		state.execute("INSERT INTO Users(UserName, PasswordHash, Salt) VALUES('"+usr+"','1','"+BCrypt.gensalt()+"')");
		ResultSet rs = state.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		return rs.getString(1);
	    } catch (Exception e) {return null;}
		finally{
			close();
		}
	}

    private String insertLecture(String prof) throws SQLException {
    	try{
		connect();
		state.execute("INSERT INTO Lectures(LectureDate,StartTime,EndTime,Professor,Room,CourseID) VALUES('1995-03-02','14','15','"+prof+"','R1','1')");
		ResultSet rs = state.executeQuery("SELECT LAST_INSERT_ID()");
		rs.next();
		return rs.getString(1);
	    } catch (Exception e) {e.printStackTrace();;return null;}
		finally{
			close();
		}
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


    
}
