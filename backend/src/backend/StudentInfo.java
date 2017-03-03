package backend;


import java.io.Serializable;

/**
 * 
 * A class for the info that students will send to the backend.
 * 
 * @author Mathias, documentation by choffa
 *
 */
public class StudentInfo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte rank;
    byte oldRank;
    String fag;
    int id;

    /**
     * Constructs a new StudentInfo object, obviously  
     * @param A string that contains the subject code
     * @param The new ranking that the student sends
     * @param The old rank that a student might have sent earlier
     */
    public StudentInfo(String fag, byte rank, byte oldRank){
        this.fag = fag;
        this.rank = rank;
        this.oldRank = oldRank;
    }

    public byte getRank() {
        return rank;
    }

    public String getFag() {
        return fag;
    }

    public void setRank(byte rank) {
        this.rank = rank;
    }

    public void setFag(String fag) {
        this.fag = fag;
    }

    public byte getOldRank() {
        return oldRank;
    }

    public void setOldRank(byte oldRank) {
        this.oldRank = oldRank;
    }


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
