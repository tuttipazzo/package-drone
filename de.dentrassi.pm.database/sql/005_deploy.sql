DROP TABLE IF EXISTS CHANNEL_DEPLOY_GROUPS;
DROP TABLE IF EXISTS DEPLOY_KEYS;
DROP TABLE IF EXISTS DEPLOY_GROUPS;

CREATE TABLE DEPLOY_GROUPS (
    ID              VARCHAR(36) NOT NULL,
    
    NAME            VARCHAR(255),
    
    PRIMARY KEY ( ID )
);

CREATE TABLE DEPLOY_KEYS (
    ID              VARCHAR(36) NOT NULL,
    
    GROUP_ID        VARCHAR(36),
    
    NAME            VARCHAR(255),
    KEY_DATA        VARCHAR(1024) NOT NULL,
    
    CREATION_TS     DATETIME NOT NULL,
    
    PRIMARY KEY ( ID ),
    FOREIGN KEY ( GROUP_ID ) REFERENCES DEPLOY_GROUPS ( ID ) ON DELETE CASCADE
);

CREATE TABLE CHANNEL_DEPLOY_GROUPS (
    CHANNEL_ID      VARCHAR(36) NOT NULL,
    GROUP_ID        VARCHAR(36) NOT NULL,
    
    PRIMARY KEY ( CHANNEL_ID, GROUP_ID ),
    FOREIGN KEY ( CHANNEL_ID ) REFERENCES CHANNELS ( ID ) ON DELETE CASCADE,
    FOREIGN KEY ( GROUP_ID ) REFERENCES DEPLOY_GROUPS ( ID ) ON DELETE CASCADE
);