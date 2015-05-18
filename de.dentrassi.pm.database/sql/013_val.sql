CREATE TABLE VALIDATION_MESSAGES (
    ID              VARCHAR(36) NOT NULL,
    "TYPE"            VARCHAR(1) NOT NULL,
    
    CHANNEL_ID      VARCHAR(36) NOT NULL,
    
    NS              VARCHAR(255) NOT NULL,
    
    SEVERITY        VARCHAR(16) NOT NULL,
    MESSAGE         LONGTEXT,
    
    PRIMARY KEY (ID),
    
    FOREIGN KEY (CHANNEL_ID) REFERENCES CHANNELS(ID) ON DELETE CASCADE
);

CREATE TABLE VAL_MSG_ARTIFACTS (
    MSG_ID          VARCHAR(36) NOT NULL,
    ARTIFACT_ID     VARCHAR(36) NOT NULL,
    
    PRIMARY KEY (MSG_ID, ARTIFACT_ID),
    
    FOREIGN KEY (MSG_ID) REFERENCES VALIDATION_MESSAGES(ID) ON DELETE CASCADE,
    FOREIGN KEY (ARTIFACT_ID) REFERENCES ARTIFACTS(ID) ON DELETE CASCADE
); 

ALTER TABLE CHANNELS ADD COLUMN AGR_NUM_WARN   NUMERIC NOT NULL DEFAULT 0;
ALTER TABLE CHANNELS ADD COLUMN AGR_NUM_ERR    NUMERIC NOT NULL DEFAULT 0;

ALTER TABLE ARTIFACTS ADD COLUMN AGR_NUM_WARN  NUMERIC NOT NULL DEFAULT 0;
ALTER TABLE ARTIFACTS ADD COLUMN AGR_NUM_ERR   NUMERIC NOT NULL DEFAULT 0;