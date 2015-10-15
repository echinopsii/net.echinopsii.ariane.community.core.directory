--
-- Procedure to migrate data from datacenter to Location
--

DELIMITER $$
CREATE PROCEDURE `migrateDatacenterToLocation`()
  BEGIN
    DECLARE FoundCount INT;

    SELECT COUNT(1) INTO FoundCount
    FROM information_schema.tables
    WHERE table_schema = 'ariane_directory'
          AND table_name = 'datacenter';

    IF FoundCount = 1 THEN
      SET @sql = CONCAT('INSERT INTO location (id, address, country, description, gpsLatitude, gpsLongitude, Name, town,type, version, zipCode) SELECT id, address, country, description, gpsLatitude, gpsLongitude, dcName, town, ''DATACENTER'', version, zipCode FROM datacenter');
      PREPARE stmt FROM @sql;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;

      SET @sql = CONCAT('INSERT INTO location_routingArea (locations_id, routingAreas_id) SELECT datacenters_id, routingAreas_id FROM datacenter_routingArea');
      PREPARE stmt FROM @sql;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;

      SET @sql = CONCAT('INSERT INTO location_subnet (locations_id, subnets_id) SELECT datacenters_id, subnets_id FROM datacenter_subnet');
      PREPARE stmt FROM @sql;
      EXECUTE stmt;
      DEALLOCATE PREPARE stmt;

      SET FOREIGN_KEY_CHECKS=0;
      DROP TABLE IF EXISTS  datacenter;
      DROP TABLE IF EXISTS  datacenter_routingArea;
      DROP TABLE IF EXISTS  datacenter_subnet;
      SET FOREIGN_KEY_CHECKS=1;

    END IF;
  END $$
DELIMITER ;


call migrateDatacenterToLocation();

--
-- Drop Procedure after usage
--

DROP PROCEDURE IF EXISTS migrateDatacenterToLocation;