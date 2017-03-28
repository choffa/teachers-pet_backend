package tests;

import frontend.Connection;
import org.junit.*;

import java.io.*;
import java.net.Socket;
import java.sql.Date;

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

    @Before
    public void setUp() throws IOException {
        s = mock(Socket.class);
        byte[] b = new byte[]{};
        in = new ByteArrayInputStream(b);
        out = new ByteArrayOutputStream();
        when(s.getOutputStream()).thenReturn(out);
        when(s.getInputStream()).thenReturn(in);
        when(s.isConnected()).thenReturn(true);
        c = new Connection(s);
    }

    @Test
    public void createLecture() throws IOException {
        out.flush();
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
    public void badRoomCreateLecture() throws IOException {
        out.flush();
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
    public void createSubject() throws IOException {
        out.flush();
        int lectureID = 12;
        String name = "EULERS_THEOREM";
        String expectedCommands = "SET_SUBJECT " + lectureID + " " + name + "\r\n";
        c.createSubject(lectureID, name);

        assertEquals(expectedCommands, out.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void badNameCreateSubject() throws IOException {
        out.flush();
        int lectureID = 12;
        String name = "EULERS THEOREM";
        c.createSubject(lectureID, name);
    }
}
