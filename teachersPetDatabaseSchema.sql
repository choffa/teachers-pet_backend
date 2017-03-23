CREATE TABLE Lectures (
    LectureID 	    INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    LectureDate     DATE NOT NULL,
    StartTime 	    INTEGER NOT NULL,
    EndTime 	      INTEGER NOT NULL,
    Professor 	    CHAR(32),
    Room            VARCHAR(16),
    CourseID        VARCHAR(8)
);

CREATE TABLE Subjects (
    SubjectID      	INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT,
    LectureID 	    INTEGER NOT NULL,
    SubjectName     VARCHAR(32),
    
    CONSTRAINT Subject_LectureID_FK FOREIGN KEY (LectureID) REFERENCES Lectures(LectureID)
																	                                  ON UPDATE CASCADE
                                                                    ON DELETE CASCADE
);

CREATE TABLE SubjectRanking (
    SubjectID    	  INTEGER NOT NULL,
    StudentID   	  CHAR(32) NOT NULL,
    Ranking     	  INTEGER NOT NULL,
    RankingComment  TEXT,

    CONSTRAINT RankingConstraint CHECK (Ranking > 0 AND Ranking < 5),
    CONSTRAINT SubjectRanking_SubjectID_FK FOREIGN KEY (SubjectID) REFERENCES Subjects(SubjectID)
																			                                  ON UPDATE CASCADE
                                                                        ON DELETE CASCADE
);

CREATE TABLE SpeedRanking (
    LectureID    	INTEGER   NOT NULL,
    StudentID   	CHAR(32)  NOT NULL,
    Ranking     	INTEGER   NOT NULL,

    CONSTRAINT RankingConstraint CHECK (Ranking > 0 AND Ranking < 5),
    CONSTRAINT SpeedRanking_LectureID_FK FOREIGN KEY (LectureID) REFERENCES Lectures(LectureID)
																		                                    ON UPDATE CASCADE
                                                                        ON DELETE CASCADE
);

CREATE TABLE Users (
    Username        CHAR(32) NOT NULL,
    PasswordHash    VARCHAR(255) NOT NULL,
    Salt            CHAR(12) NOT NULL
);