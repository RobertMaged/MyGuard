--Get scanned schedules for every session - outdated.
SELECT schedule_session.id, schedule_session.created, schedules.id, schedules.query, schedules.query_type, schedules.created FROM schedule_session JOIN session_schedule_xref ON session_schedule_xref.session_id = schedule_session.id JOIN schedules ON schedules.id = session_schedule_xref.schedule_id;


--GET Schedule with its Query by id, two queries has the same result.
SELECT * FROM schedules as s INNER JOIN queries as q ON q.id = s.query_id WHERE s.id = ?;
--or
SELECT s.id, s.query_id, s.is_muted, s.created, q.id, q.content,q.uuid,q.hint, q.type FROM schedules as s INNER JOIN queries as q ON q.id = s.query_id WHERE s.id = ?;


--get new breaches of schedule which user didn't acknowledge.
SELECT b.* FROM breaches as b INNER JOIN schedule_scan_record as r ON r.breach_id = b.id WHERE r.schedule_id =? AND r.user_acknowledged = FALSE;
--get all breaches of schedule by id
SELECT b.* FROM breaches as b INNER JOIN schedule_scan_record as r ON r.breach_id = b.id WHERE r.schedule_id = ?;
--or
SELECT b.* FROM breaches as b WHERE b.id IN (SELECT r.breach_id from schedule_scan_record as r WHERE r.schedule_id = ?);


--may be create index on scan_sessions.created, because its is used to get last session.
--Get Last SESSION UN NOTIFIED leaks.
SELECT COUNT(b.title) FROM breaches as b INNER JOIN schedule_scan_record as r ON r.breach_id = b.id WHERE r.session_id = (SELECT id FROM (SELECT c.id, MAX(c.created) FROM scan_sessions as c)) AND r.user_notified = FALSE;

--Get Schedule Unacknowledged Breaches
SELECT s.*, b.* FROM schedules as s INNER JOIN queries as q ON q.id = s.query_id INNER JOIN schedule_scan_record AS r ON s.id = r.schedule_id AND r.user_acknowledged = FALSE INNER JOIN breaches as b ON r.breach_id = b.id;


--GEt scan records of a schedule
SELECT r.* FROM schedule_scan_record AS r WHERE r.schedule_id = ?;