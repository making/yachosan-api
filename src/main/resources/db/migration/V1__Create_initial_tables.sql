CREATE TABLE y_participant (
  nickname    VARCHAR(255) NOT NULL,
  schedule_id VARCHAR(36),
  comment     VARCHAR(200),
  email       VARCHAR(127),
  password    VARCHAR(255),
  PRIMARY KEY (nickname, schedule_id)
);

CREATE TABLE y_participant_replies (
  nickname    VARCHAR(255) NOT NULL,
  schedule_id VARCHAR(36)  NOT NULL,
  replies     VARCHAR(10),
  start_date  DATE,
  PRIMARY KEY (nickname, schedule_id, start_date)
);

CREATE TABLE y_schedule (
  schedule_id          VARCHAR(36)  NOT NULL,
  aikotoba             VARCHAR(255),
  created_at           TIMESTAMP    NOT NULL,
  schedule_description VARCHAR(200) NOT NULL,
  schedule_name        VARCHAR(50)  NOT NULL,
  updated_at           TIMESTAMP    NOT NULL,
  PRIMARY KEY (schedule_id)
);

CREATE TABLE y_schedule_proposed_dates (
  schedule_id VARCHAR(36) NOT NULL,
  start_date  DATE
);

CREATE INDEX UK_5ax1ut9eagrcy54x53e8695ho ON y_schedule (updated_at);

CREATE INDEX UK_fpp1fakbj1pj4o4gmsn6dl21u ON y_schedule_proposed_dates (start_date);

ALTER TABLE y_participant
ADD CONSTRAINT FK_mipaa3b1c3j9an139a5las2u3
FOREIGN KEY (schedule_id)
REFERENCES y_schedule;

ALTER TABLE y_participant_replies
ADD CONSTRAINT FK_igsgo928ntl6gj5gbm00tp6oe
FOREIGN KEY (nickname, schedule_id)
REFERENCES y_participant;

ALTER TABLE y_schedule_proposed_dates
ADD CONSTRAINT FK_q2hdf0o0jrdm5q25aktquxgmf
FOREIGN KEY (schedule_id)
REFERENCES y_schedule;