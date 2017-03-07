package test;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import frontend.Connection;

public class ConnectionTest {

	public static void main(String[] args) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter("Hello.txt");
		pw.append("This is added");
		pw.close();
		pw.append("This isn\'t");

	}
}
