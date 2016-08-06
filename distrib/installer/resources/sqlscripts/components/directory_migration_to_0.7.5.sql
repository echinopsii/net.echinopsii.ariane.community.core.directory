ALTER TABLE `niCard` CHANGE COLUMN `ripAddress_id` `ipAddress_id` BIGINT(20);
ALTER TABLE `niCard` CHANGE COLUMN `rosInstance_id` `osInstance_id` BIGINT(20);
RENAME TABLE niCard TO nic;