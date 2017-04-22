package tests;

import backend.ServerDatabaseConnection;
import org.junit.*;

import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

import static org.junit.Assert.*;

/**
 * Created by Choffa on 13-Apr-17.
 */
public class DatabaseConnectionTest {

    private ServerDatabaseConnection sdbc;
    private static Connection dbcon;
    private static String url = "jdbc:mysql://127.0.0.1/test_teacherspet";
    //private static String baseUrl = "jdbc:mysql://localhost:3306?useSSL=false";
    private static String user = "root";
    private static String pw = "";

    @BeforeClass
    public static void setUpClass() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            dbcon = DriverManager.getConnection(url,user,pw);
            /*Statement s = dbcon.createStatement();
            s.execute("CREATE SCHEMA test_teacherspet;");
            s.execute("USE test_teacherspet;");
            Scanner scanner = new Scanner(new FileInputStream("teachersPetDatabaseSchema.sql"));
            String sql = "";
            scanner.useDelimiter(";");
            while (scanner.hasNext()){
                sql = scanner.next();
                s.execute(sql);
            }
            System.out.println(sql);*/
        } catch (Exception ex) {
            fail("Could not connect test database properly");
        }
    }

    @Before
    public void setUp() throws Exception {
        sdbc = new ServerDatabaseConnection(url, user, pw);
    }

    @Test
    public void testInsert() throws Exception {
        testLectureInsertion();
        testSpeedRankingInsertion();
    }

    private void testLectureInsertion() throws Exception {
        String[] args = {"2020-01-01", "14", "16", "qwertyuioplkjhgfdsazxcvbnmnbvcxz", "R59", "SHREK101"};
        sdbc.insert(ServerDatabaseConnection.LECTURES, args);
        //Statement s = dbcon.createStatement();
        //ResultSet res = s.executeQuery("SELECT * FROM test_teacherspet.lectures");
        //res.next();
        //for (int i = 0; i < args.length; i++) {
        //    assertEquals(args[i], res.getString(i+2));
        //}
        compare(args, "Lectures", "Lecture input not as expected", 2);
    }

    private void testSpeedRankingInsertion() throws Exception {
        String[] args = {"1", "4", "qwertyuioplkjhgfdsazxcvbnmnbvcxz"};
        sdbc.insert(ServerDatabaseConnection.SPEEDRANKING, args);
        Statement s = dbcon.createStatement();
        ResultSet res = s.executeQuery("SELECT * FROM Speedranking");
        res.next();
        for(int i = 0; i < args.length; i++) {
            assertEquals(args[i], res.getString(i+1));
        }
    }

    private void testSubjectInsertion() throws Exception {
        String[] args = {"1", "4", "This is a rather long string of text isnt it?"};
        sdbc.insert(ServerDatabaseConnection.SUBJECTS, args);
        Statement s = dbcon.createStatement();
        ResultSet r = s.executeQuery("");
    }

    private void testSubjectRankingInsertion() throws Exception {
        String[] args = {};
    }

    //TODO: use hashmap to avoid dependece on order
    private void compare(String[] args, String table, String message, int resultCorrection) throws Exception {
        Statement s = dbcon.createStatement();
        String sql = "SELECT * FROM " + table;
        ResultSet r = s.executeQuery(sql);
        r.next();
        for(int i = 0; i < args.length; i++) {
            assertEquals(message, args[i], r.getString(i+resultCorrection));
        }
    }

    @After
    public void tearDown() throws Exception {
        sdbc.close();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Statement s = dbcon.createStatement();
        s.execute("DROP SCHEMA test_teacherspet");
        dbcon.close();
    }
}
