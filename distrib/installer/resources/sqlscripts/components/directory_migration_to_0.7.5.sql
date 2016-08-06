ALTER TABLE `niCard` CHANGE COLUMN `ripAddress_id` `ipAddress_id` BIGINT(20);
ALTER TABLE `niCard` CHANGE COLUMN `rosInstance_id` `osInstance_id` BIGINT(20);
ALTER TABLE `niCard` CHANGE COLUMN `name` `name` VARCHAR(255) NOT NULL;
ALTER TABLE `niCard` ADD UNIQUE `UK_2692cl4g69h2f25y25kryGer7` (`name`);
RENAME TABLE niCard TO nic;

ALTER TABLE `ipaddress` DROP FOREIGN KEY `FK_3hswljwlt747k3jjs9qv7bn11`;
ALTER TABLE `ipaddress` DROP COLUMN `niCard_id`;
RENAME TABLE ipaddress TO ipAddress;

UPDATE resource SET resourceName="dirComITiNtwNIC" WHERE resourceName="dirComITiNtwNICard";
UPDATE permission SET permissionName="dirComITiNtwNIC:display" WHERE permissionName="dirComITiNtwNICard:display";
UPDATE permission SET permissionName="dirComITiNtwNIC:create" WHERE permissionName="dirComITiNtwNICard:create";
UPDATE permission SET permissionName="dirComITiNtwNIC:remove" WHERE permissionName="dirComITiNtwNICard:remove";
UPDATE permission SET permissionName="dirComITiNtwNIC:update" WHERE permissionName="dirComITiNtwNICard:update";