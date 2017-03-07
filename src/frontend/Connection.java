package frontend;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalTime;

public class Connection implements Closeable, AutoCloseable {

	private final int PORT = 4728;
	private final String HOST = "localhost";	
	private Socket socket;
	private PrintWriter out;
	private Scanner in;
	private boolean isClosed;
	
	//------------------------------------------------------------------------
	//The connection stuff
	
	/**
	 * A method that sets up the connection to the server
	 * @throws IOException
	 */
	public Connection() throws IOException {
		isClosed = false;
		socket = new Socket(HOST, PORT);
		out = new PrintWriter(socket.getOutputStream());
		in = new Scanner(socket.getInputStream());
	}
	
	/**
	 * A method that closes the connection to the server
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		isClosed = true;
		out.println("CLOSE");
		out.flush();
		out.close();
		in.close();
		socket.close();
	}

	/**
	 * A method that checks is the connection has been closed
	 * @return The result of Socket.isclosed()
	 */
	public boolean isClosed(){
		return this.isClosed;
	}
	
	//------------------------------------------------------------------------
	//The subject rating stuff
	
	/**
	 * A method for sending a subject rating to the server
	 * @param subjectID The ID of the subject that is to be rated
	 * @param studentID The ID of the student that sent this rating
	 * @param rating The actual rating of the subject, this is a number between 1 and 5 (inclusive)
	 * @throws IllegalArgumentException This is thrown if the ranking is wrong
	 * @throws IllegalStateException This is thrown if the connection isn't active
	 */
	public void sendSubjectRating(int subjectID, int studentID, int rating, String comment) 
			throws IllegalArgumentException, IllegalStateException {
		if (isClosed | !socket.isConnected()){
			throw new IllegalStateException("Socket is not connected, please run Connection.connect() first");
		}
		if (rating < 1 || rating > 5){
			throw new IllegalArgumentException("Rating must be between 1 and 5");
		}
		
		out.println("SET_SUBJECTRATING " + subjectID + " " + studentID + " " + rating + " " + comment);
		out.flush();
	}
	
	/**
	 * A method that requests the subjectRating from a specific subject 
	 * @param subjectID The subjectID of the subject in question
	 * @return A float that represents the average rating for the subject
	 */
	public float getAverageSubjectRating(int subjectID){
		out.println("GET_AVERAGESUBJECTRATING");
		out.flush();
		return in.nextFloat();
	}
	
	//------------------------------------------------------------------------
	//The lecture stuff
	
	/**
	 * A method that request the currently active lectures
	 * @return An ArrayList with the lectures in it
	 */
	public ArrayList<Object> getLectures(){
		//TODO: Create method for receiving the lectures from the server
		return null;
	}

	/**
	 * A method that request the lectures by a specific professor
	 * @param professorID The ID of the professor that held the lectures
	 * @return An ArrayList with the lectures in it
	 */
	public ArrayList<Object> getLectures(int professorID){
		//TODO: Create method for getting lectures by specific professor
		return null;
	}
	
	/**
	 * A method for creating a new lecture in the database
	 * @param date The date that the lecture takes place
	 * @param start The time the lecture starts
	 * @param end The end time the lecture ends
	 * @param room The room that the lecture takes place
	 */
	public void createLecture(int professorID, LocalDate date, LocalTime start, LocalTime end, String room){
		//TODO: Make method body for 
	}
	
	//------------------------------------------------------------------------
	//The speed rating stuff
	
	/**
	 * A method for sending a speed rating to the server
	 * @param lectureID The ID of the lecture being rated on
	 * @param studentID The ID of the student performing the rating
	 * @param rating The actual rating of the speed. This should be a number between 1 and 5 (inclusive)
	 * @throws IllegalArgumentException if the rating is not between 1 and 5
	 */
	public void sendSpeedRating(int lectureID, int studentID, int rating) throws IllegalArgumentException{
		//TODO: Create method for sending speed-rating
	}
	
	/**
	 * A method that request the speed rating for specific lecture
	 * @param lectureID The ID of the lecture to get the rating for
	 * @return A float representing the current average speed rating
	 */
	public float getAverageSpeedRating(int lectureID) {
		//TODO: Create method for getting speed-rating for specific lecture
		return 0;
	}
	
	//------------------------------------------------------------------------
	//the subject stuff
	
	/**
	 * A method that requests the subjects associated with a specific lecture
	 * @param lectureID The ID of the lecture to get subjects for
	 * @return A ArrayList of integers representing the ID of the subjects
	 */
	public ArrayList<Integer> getSubjects(int lectureID) {
		//TODO: Create method for getting the subjects for specific lectures
		return null;
	}
	
	/**
	 * A method for creating a new subject associated to a lecture in the database
	 * @param lectureID The ID of the lecture to associate the subject with
	 */
	public void createSubject(int lectureID) {
		//TODO: Create method for creating subject associated with specific lecture
	}
	
}
