CREATE TABLE Lectures (
    LectureID 	    INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    LectureDate     DATE NOT NULL,
    StartTime 	    INTEGER NOT NULL,
    EndTime 	      INTEGER NOT NULL,
    Professor 	    CHAR(32),
    Room            VARCHAR(32),
    CourseID        VARCHAR(32)
);

CREATE TABLE Subjects (
    SubjectID      	INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    LectureID 	    INTEGER NOT NULL,
    SubjectName     VARCHAR(32),
    Comment			VARCHAR(255),
	
    CONSTRAINT Subject_LectureID_FK FOREIGN KEY (LectureID) REFERENCES Lectures(LectureID)
																	                                  ON UPDATE CASCADE
                                                                    ON DELETE CASCADE
);

CREATE TABLE SubjectRanking (
    SubjectID    	  INTEGER NOT NULL,
    StudentID   	  CHAR(32) NOT NULL,
    Ranking     	  INTEGER NOT NULL,
    RankingComment  TEXT,

    CONSTRAINT SubjectRanking_SubjectID_FK FOREIGN KEY (SubjectID) REFERENCES Subjects(SubjectID),
																			                                  ON UPDATE CASCADE
                                                                        ON DELETE CASCADE
);

CREATE TABLE SpeedRanking (
    LectureID    	INTEGER   NOT NULL,
    StudentID   	CHAR(32)  NOT NULL,
    Ranking     	INTEGER   NOT NULL,

    CONSTRAINT SpeedRanking_LectureID_FK FOREIGN KEY (LectureID) REFERENCES Lectures(LectureID)
	
																		                                    ON UPDATE CASCADE
                                                                        ON DELETE CASCADE
);

CREATE TABLE Users (
    Username        CHAR(32) NOT NULL,
    PasswordHash    VARCHAR(255) NOT NULL,
    Salt            CHAR(29) NOT NULL
);

CREATE TABLE LectureComment (
	LectureID	INTEGER NOT NULL,
	Comment		VARCHAR(255),
	
	CONSTRAINT LID_FK FOREIGN KEY (LectureID) REFERENCES Lectures(LectureID) ON UPDATE CASCADE ON DELETE CASCADE
	);