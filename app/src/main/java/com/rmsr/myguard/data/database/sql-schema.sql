CREATE TABLE `queries` (
`_queryId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`que_content` TEXT NOT NULL COLLATE NOCASE,
`que_type` TEXT NOT NULL,
`que_uuid` BLOB NOT NULL,
`que_hint` TEXT DEFAULT NULL
);

CREATE UNIQUE INDEX `index_queries_que_uuid` ON `queries` (`que_uuid`);

CREATE TABLE `breaches` (
`_breachId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`bre_name` TEXT NOT NULL COLLATE NOCASE,
`bre_created` INTEGER NOT NULL,
`bre_logoPath` TEXT NOT NULL,
`bre_title` TEXT NOT NULL COLLATE NOCASE,
`bre_description` TEXT NOT NULL,
`bre_domain` TEXT NOT NULL COLLATE NOCASE,
`bre_discoveredDate` TEXT NOT NULL,
`bre_pwnCount` INTEGER NOT NULL,
`bre_compromisedData` TEXT NOT NULL
);

CREATE UNIQUE INDEX `index_breaches_bre_name` ON `breaches` (`bre_name`);

CREATE TABLE `search_history` (
`his_query_id` INTEGER NOT NULL, `his_created` INTEGER NOT NULL,
`his_accessed` INTEGER NOT NULL,
`his_accessCount` INTEGER NOT NULL,
PRIMARY KEY(`his_query_id`),
FOREIGN KEY(`his_query_id`) REFERENCES `queries`(`_queryId`) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX `index_search_history_his_query_id` ON `search_history` (`his_query_id`);

CREATE TABLE `history_breach_xref` (
`his_x_bre__query_id` INTEGER NOT NULL,
`his_x_bre__breach_id` INTEGER NOT NULL,
PRIMARY KEY(`his_x_bre__query_id`, `his_x_bre__breach_id`),
FOREIGN KEY(`his_x_bre__breach_id`) REFERENCES `breaches`(`_breachId`) ON UPDATE NO ACTION ON DELETE CASCADE,
FOREIGN KEY(`his_x_bre__query_id`) REFERENCES `search_history`(`his_query_id`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE `schedules` (
`_scheduleId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`sch_query_id` INTEGER NOT NULL,
`sch_isMuted` INTEGER NOT NULL DEFAULT FALSE,
`sch_created` INTEGER NOT NULL,
FOREIGN KEY(`sch_query_id`) REFERENCES `queries`(`_queryId`) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE UNIQUE INDEX `index_schedules_sch_query_id` ON `schedules` (`sch_query_id`);

CREATE TABLE `scan_sessions` (
`_sessionId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
`scn_created` INTEGER NOT NULL,
`scn_started` INTEGER NOT NULL,
`scn_reqStarted` INTEGER NOT NULL,
`scn_reqEnded` INTEGER NOT NULL,
`scn_requestsAvg` INTEGER NOT NULL,
`scn_ended` INTEGER NOT NULL,
`scn_responded` INTEGER NOT NULL
);

CREATE TABLE `scan_schedule_xref` (
`scn_x_sch__session_id` INTEGER NOT NULL,
`scn_x_sch__schedule_id` INTEGER NOT NULL,
PRIMARY KEY `scn_x_sch__session_id`, `scn_x_sch__schedule_id`),
FOREIGN KEY(`scn_x_sch__session_id`) REFERENCES `scan_sessions`(`_sessionId`) ON UPDATE NO ACTION ON DELETE CASCADE,
FOREIGN KEY(`scn_x_sch__schedule_id`) REFERENCES `schedules`(`_scheduleId`) ON UPDATE NO ACTION ON DELETE CASCADE
);

CREATE TABLE `schedule_scan_record` (
`rec_schedule_id` INTEGER NOT NULL,
`rec_breach_id` INTEGER NOT NULL,
`rec_session_id` INTEGER,
`rec_notified` INTEGER NOT NULL,
`rec_notifyTime` INTEGER NOT NULL,
`rec_acknowledged` INTEGER NOT NULL,
`rec_acknowledgeTime` INTEGER NOT NULL,
PRIMARY KEY`rec_schedule_id`, `rec_breach_id`),
FOREIGN KEY(`rec_session_id`) REFERENCES `scan_sessions`(`_sessionId`) ON UPDATE CASCADE ON DELETE SET NULL,
FOREIGN KEY(`rec_schedule_id`) REFERENCES `schedules`(`_scheduleId`) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX `index_schedule_scan_record_rec_session_id` ON `schedule_scan_record` (`rec_session_id`);
