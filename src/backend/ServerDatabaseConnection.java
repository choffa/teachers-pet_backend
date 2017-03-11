package backend;

import java.sql.DriverManager;
import java.sql.SQLException;
import com.mysql.jdbc.Connection;


public class ServerDatabaseConnection {
public void connect(){
Connection con  =  null;
try {
  Class.forName("com.mysql.jdbc.Driver").newInstance();
  String url = "jdbc:mysql://mysql.idi.ntnu.no/mindatabase";
  String user = "brukernavn";
  String pw = "passord";
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
} finally {
    try {
      if (con !=  null) con.close();
    } catch (SQLException ex) {
      System.out.println("Epic fail: "+ex.getMessage());
    }
  }
con.
  }
}
