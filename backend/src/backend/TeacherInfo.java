package backend;

import java.io.Serializable;

/**
 * A class for the information that the professor will recieve from the server backend
 * 
 * @author Mathias, documentation by choffa
 *
 */
public class TeacherInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	float snitt;
    int antall;

    /**
     * Constructs a new TeacherInfo object, obviously
     * @param The current average rating
     * @param The number or student that have rated
     */
    public TeacherInfo(float snitt, int antall) {
        this.snitt = snitt;
        this.antall = antall;
    }

    public float getSnitt() {
        return snitt;
    }

    public void setSnitt(float snitt) {
        this.snitt = snitt;
    }

    public int getAntall() {
        return antall;
    }

    public void setAntall(int antall) {
        this.antall = antall;
    }
    
    public String toString(){
    	String ret = "Mean is: "+snitt+" and number of people is:  "+antall;
    	return ret;
    }
}