INSERT INTO y_schedule (schedule_id, aikotoba, created_at, schedule_description, schedule_name, updated_at)
VALUES ('550e8400-e29b-41d4-a716-446655440000', NULL, now(), 'This is a sample.', 'sample meeting', now());

INSERT INTO y_schedule_proposed_dates (schedule_id, start_date)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '2014-07-01');
INSERT INTO y_schedule_proposed_dates (schedule_id, start_date)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '2014-07-02');
INSERT INTO y_schedule_proposed_dates (schedule_id, start_date)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '2014-07-03');
INSERT INTO y_schedule_proposed_dates (schedule_id, start_date)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '2014-07-04');
INSERT INTO y_schedule_proposed_dates (schedule_id, start_date)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '2014-07-05');
INSERT INTO y_schedule_proposed_dates (schedule_id, start_date)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '2014-07-06');
INSERT INTO y_schedule_proposed_dates (schedule_id, start_date)
VALUES ('550e8400-e29b-41d4-a716-446655440000', '2014-07-07');

INSERT INTO y_participant (nickname, schedule_id, comment, email, password)
VALUES ('xxx', '550e8400-e29b-41d4-a716-446655440000', NULL, NULL, NULL);
INSERT INTO y_participant (nickname, schedule_id, comment, email, password)
VALUES ('yyy', '550e8400-e29b-41d4-a716-446655440000', NULL, NULL, NULL);

INSERT INTO y_participant_replies (nickname, schedule_id, replies, start_date)
VALUES ('xxx', '550e8400-e29b-41d4-a716-446655440000', 'OK', '2014-07-01');
INSERT INTO y_participant_replies (nickname, schedule_id, replies, start_date)
VALUES ('xxx', '550e8400-e29b-41d4-a716-446655440000', 'OK', '2014-07-02');
INSERT INTO y_participant_replies (nickname, schedule_id, replies, start_date)
VALUES ('xxx', '550e8400-e29b-41d4-a716-446655440000', 'NG', '2014-07-03');


INSERT INTO y_participant_replies (nickname, schedule_id, replies, start_date)
VALUES ('yyy', '550e8400-e29b-41d4-a716-446655440000', 'NG', '2014-07-01');
INSERT INTO y_participant_replies (nickname, schedule_id, replies, start_date)
VALUES ('yyy', '550e8400-e29b-41d4-a716-446655440000', 'OK', '2014-07-02');
INSERT INTO y_participant_replies (nickname, schedule_id, replies, start_date)
VALUES ('yyy', '550e8400-e29b-41d4-a716-446655440000', 'NG', '2014-07-03');