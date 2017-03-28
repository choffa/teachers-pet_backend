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
    OutputStream out;

    @Before
    public void setUp() throws IOException {
        s = mock(Socket.class);
        //byte[] b = new byte[]{};
        //in = new ByteArrayInputStream(b);
        out = new ByteArrayOutputStream();
        when(s.getOutputStream()).thenReturn(out);
        //c = new Connection(s);
    }

    @Test
    public void createLecture() throws IOException {
        String professorID = "abcdefghijklmnopqrst";
        String courseID = "TDT4140";
        @SuppressWarnings("deprecation")
        Date date = new Date(120, 1, 1);
        int start = 12;
        int end = 14;
        String room = "R42";
        String expectedCommands = "SET_LECTURE " + professorID + " " + courseID + " " + date + " " + start + " " + end + " " + room;
        byte[] expectedBytes = expectedCommands.getBytes("UTF-8");
        c = new Connection(s);
        c.createLecture(professorID, courseID, date, start, end, room);

        assertEquals(out[0], expectedBytes[0]);
    }
}
