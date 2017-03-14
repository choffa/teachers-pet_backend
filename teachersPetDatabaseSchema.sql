CREATE TABLE Lectures (
    LectureID 	    INTEGER PRIMARY KEY NOT NULL AUTO_INCREMENT,
    LectureDate     DATE NOT NULL,
    StartTime 	    TIME NOT NULL,
    EndTime 	    TIME NOT NULL,
    Professor 	    TEXT
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
    StudentID   	  TEXT NOT NULL,
    Ranking     	  INTEGER NOT NULL,
    RankingComment  TEXT,

    CONSTRAINT RankingConstraint CHECK (Ranking > 1 AND Ranking < 5),
    CONSTRAINT SubjectRanking_SubjectID_FK FOREIGN KEY (SubjectID) REFERENCES Subjects(SubjectID)
																			ON UPDATE CASCADE
                                                                            ON DELETE CASCADE
);

CREATE TABLE SpeedRanking (
    LectureID    	INTEGER NOT NULL,
    StudentID   	TEXT NOT NULL,
    Ranking     	INTEGER NOT NULL,

    CONSTRAINT RankingConstraint CHECK (Ranking > 0 AND Ranking < 5),
    CONSTRAINT SpeedRanking_LectureID_FK FOREIGN KEY (LectureID) REFERENCES Lectures(LectureID)
																		                                    ON UPDATE CASCADE
                                                                        ON DELETE CASCADE
);