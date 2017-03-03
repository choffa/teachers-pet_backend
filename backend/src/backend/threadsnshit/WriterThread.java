package backend.threadsnshit;
import backend.*;
import backend.database.*;
import backend.tests.ReadWriteTest;

import java.util.ArrayList;

/**
 * A class that handles the writing to the database object. It implements
 * Runnable & ServerListener
 * 
 * @author Mathias, documentation by choffa
 *
 */
public class WriterThread implements Runnable, ServerListener{
	InputDatabase idb;
	volatile ArrayList<StudentInfo> infoStack;
	
	/**
	 * Creates a new instance of the writerThread
	 * @param The Database object that the WriterThread should write to
	 */
	public WriterThread(InputDatabase db){
		idb = db;
		infoStack = new ArrayList<StudentInfo>();
	}
	
	@Override
	public void run() {
		while(true){
			if(infoStack.size()>0){
				if(infoStack.get(0)!=null){
					write(infoStack.get(0));
					infoStack.remove(0);
				}
			}
		}
	}

	
	@Override
	/**
	 * Method for adding StudentInfo to the stack
	 * @param The studentInfo to add
	 */
	public synchronized void addInfo(StudentInfo s) {
		infoStack.add(s);
		
	}
	
	
	private synchronized void write(StudentInfo s){
		if(s!= null){
			if(s.getOldRank()==0){
				addNewRank(s);
			} else {
				updateOldRank(s);
			}
		} 
	}
	/**
	 * Adds a new ranking to the database object
	 * @param The StudentInfo object that contains the new ranking
	 */
	private void addNewRank(StudentInfo s){
		float oldSnitt = idb.getGjennomsnitt();
		int oldAnt = idb.getAntall();
		int newAnt = oldAnt+1;
		float newSnitt = (oldSnitt*oldAnt+s.getRank())/newAnt;
		idb.setGjennomsnitt(newSnitt);
		idb.setAntall(newAnt);
	}
	
	/**
	 * Updates the rank if the student has already voted
	 * @param The StudentInfo object that contains the updated ranking
	 */
	private void updateOldRank(StudentInfo s){
		float oldSnitt = idb.getGjennomsnitt();
		int oldAnt = idb.getAntall();
		float newSnitt = (oldSnitt*oldAnt+s.getRank()-s.getOldRank())/oldAnt;
		if(newSnitt<0){
			idb.setGjennomsnitt(0);
			return;
		}
		idb.setGjennomsnitt(newSnitt);
		
	}
	
}
