package tests;

import frontend.Connection;
import frontend.Lecture;
import org.junit.*;

import java.io.*;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Choffa on 28-Mar-17.
 */
public class ConnectionTest {

    Connection c;
    Socket s;
    InputStream in;
    OutputStream out;

    double average;
    int lectureID;

    String averageString;

    @Before
    public void setUp() throws IOException {
        s = mock(Socket.class);
        String responses = "1.54 END END 4.355";
        byte[] b = responses.getBytes();
        in = new ByteArrayInputStream(b);
        out = new ByteArrayOutputStream();
        when(s.getOutputStream()).thenReturn(out);
        when(s.getInputStream()).thenReturn(in);
        when(s.isConnected()).thenReturn(true);
        c = new Connection(s);

        Random r = new Random();
        average = r.nextDouble() * 5.0;
        lectureID = r.nextInt();

        averageString = Double.toString(average);
    }

    @Test
    public void createSubject() {
        int lectureID = 12;
        String name = "EULERS_THEOREM";
        String expectedCommands = "SET_SUBJECT " + lectureID + " " + name + "\r\n";
        c.createSubject(lectureID, name);

        assertEquals(expectedCommands, out.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void badNameCreateSubject() {
        int lectureID = 12;
        String name = "EULERS THEOREM";
        c.createSubject(lectureID, name);
    }

    @Test
    public void sendSubjectRating() {
        int subjectID = 123;
        String studentID = "fhkdsjlfhk";
        int rating = 5;
        String comment = "THISISNICE";
        String expectedCommand = "SET_SUBJECTRATING " + subjectID +  " " + studentID + " " + rating + " " + comment +"\r\n";
        c.sendSubjectRating(subjectID, studentID, rating, comment);

        assertEquals(expectedCommand, out.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void badRatingSendSubjectRating() {
        int subjectID = 123;
        String studentID = "fhkdsjlfhk";
        int rating = 100;
        String comment = "THIS IS NOT NICE";
        String expectedCommand = "SET_SUBJECTRATING " + subjectID +  " " + studentID + " " + rating + " " + comment +"\r\n";
        c.sendSubjectRating(subjectID, studentID, rating, comment);

        assertEquals(expectedCommand, out.toString());
    }

    @Test
    public void getAverageSubjectRating() {
        int subjectID = 1234;
        String expectedCommands = "GET_AVERAGESUBJECTRATING " + subjectID + "\r\n";
        double rating = c.getAverageSubjectRating(subjectID);
        double expectedResponse = 1.54;

        assertEquals(expectedCommands, out.toString());
        assertEquals(expectedResponse, rating, 0.001);
    }

    @Test
    public void getNoLectures() {
        String expectedCommans = "GET_ALLLECTURES\r\n";
        ArrayList<Lecture> lectureList = c.getLectures();

        assertEquals(expectedCommans, out.toString());
        assertTrue(lectureList.isEmpty());
    }

    //TODO: Add test with actual lecture input

    @Test
    public void getNoLecturesFromProfessorID () {
        String professorID = "jfkdsløafjkdslø";
        String expectedCommands = "GET_LECTURE " + professorID + "\r\n";
        ArrayList<Lecture> lectureList = c.getLectures(professorID);

        assertEquals(expectedCommands, out.toString());
        assertTrue(lectureList.isEmpty());
    }

    //TODO: Add test with actual lecture input

    @Test
    public void createLecture() {
        String professorID = "abcdefghijklmnopqrst";
        String courseID = "TDT4140";
        @SuppressWarnings("deprecation")
        Date date = new Date(120, 1, 1);
        int start = 12;
        int end = 14;
        String room = "R42";
        String expectedCommands = "SET_LECTURE " + professorID + " " + courseID + " " + date + " " + start + " " + end + " " + room + "\r\n";
        c.createLecture(professorID, courseID, date, start, end, room);

        assertEquals(expectedCommands, out.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void badRoomCreateLecture() {
        String professorID = "abcdefghijklmnopqrst";
        String courseID = "TDT4140";
        @SuppressWarnings("deprecation")
        Date date = new Date(120, 1, 1);
        int start = 12;
        int end = 14;
        String room = "R42 fjkdsløa";
        c.createLecture(professorID, courseID, date, start, end, room);
    }

    @Test
    public void createLectureFromObject() {
        String professorID = "abcdefghijklmnopqrst";
        String courseID = "TDT4140";
        @SuppressWarnings("deprecation")
        Date date = new Date(120, 1, 1);
        int start = 12;
        int end = 14;
        String room = "R42";
        Lecture lecture = new Lecture(-1, professorID, courseID, start, end, room, date);
        String expectedCommands = "SET_LECTURE " + professorID + " " + courseID + " " + date + " " + start + " " + end + " " + room + "\r\n";
        c.createLecture(lecture);

        assertEquals(expectedCommands, out.toString());
    }

    @Test
    public void sendSpeedRating() {
        int lectureID = 13245;
        String studentID = "jfkdsløafjaklø3402##";
        int rating = 3;
        String expectedCommands = "SET_SPEEDRATING " + lectureID + " " + rating + " " + studentID + "\r\n";
        c.sendSpeedRating(lectureID, studentID, rating);

        assertEquals(expectedCommands, out.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void badRatingSendSpeedRating() {
        int lectureID = 123445;
        String studentID = "jfkdsløfjdskløfsaQ";
        int rating = 12;
        String expectedCommands = "SET_SPEEDRATING " + lectureID + " " + studentID + " " + rating + "\r\n";
        c.sendSpeedRating(lectureID, studentID, rating);
    }

    @Test
    public void testGetAverageSpeedRating() throws IOException {
        String response = averageString;
        setUp(response);
        getAverageSpeedRating();
    }

    private void getAverageSpeedRating() {
        String expectedCommands = "GET_AVERAGESPEEDRATING " + lectureID + "\r\n";
        double response = c.getAverageSpeedRating(lectureID);

        assertEquals(expectedCommands, out.toString());
        assertEquals(average, response, 0.01);
    }

    @Test
    public void allCommandsAtOnce() throws IOException {
        String responses = averageString;
        setUp(responses);
        getAverageSpeedRating();
    }

    private void setUp(String response) throws IOException {
        //TODO: Remove once methods have been rewritten
        c.close();
        
        byte[] b = response.getBytes();
        in = new ByteArrayInputStream(b);
        out = new ByteArrayOutputStream();
        when(s.getOutputStream()).thenReturn(out);
        when(s.getInputStream()).thenReturn(in);
        when(s.isConnected()).thenReturn(true);
        c = new Connection(s);
    }

    @After
    public void tearDown() throws IOException {
        c.close();
    }
}








