--
-- Table structure for table `company`
--

CREATE TABLE IF NOT EXISTS `company` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) DEFAULT NULL,
  `companyName` VARCHAR(255) DEFAULT NULL,
  `version`     INT(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h7w1mkrsh1wcg5dkv6wrvam5m` (`companyName`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =4
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `location`
--
CREATE TABLE IF NOT EXISTS `location` (
  `id`           BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `address`      VARCHAR(255) NOT NULL,
  `country`      VARCHAR(255) NOT NULL,
  `description`  VARCHAR(255) DEFAULT NULL,
  `gpsLatitude`  DOUBLE       NOT NULL,
  `gpsLongitude` DOUBLE       NOT NULL,
  `Name`         VARCHAR(255) NOT NULL,
  `town`         VARCHAR(255) NOT NULL,
  `type`         VARCHAR(255) NOT NULL,
  `version`      INT(11) DEFAULT NULL,
  `zipCode`      BIGINT(20)   NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gwnrcuk3s0q7j5jo6cao17hn0` (`Name`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;
--
-- Table structure for table `environment`
--

CREATE TABLE IF NOT EXISTS `environment` (
  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT,
  `description`     VARCHAR(255) DEFAULT NULL,
  `environmentName` VARCHAR(255) DEFAULT NULL,
  `version`         INT(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_j9w5yy2xvayt691yivqxisw5v` (`environmentName`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =2
  DEFAULT CHARSET =latin1;

--
-- Alter Table structure for table `environment`
--

ALTER TABLE `environment` ADD `environmentCC` VARCHAR(255) DEFAULT NULL;

--
-- Table structure for table `routingArea`
--

CREATE TABLE IF NOT EXISTS `routingArea` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) DEFAULT NULL,
  `rareaName`   VARCHAR(255) DEFAULT NULL,
  `type`        VARCHAR(255) DEFAULT NULL,
  `multicast`   VARCHAR(255) DEFAULT NULL,
  `version`     INT(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_3m10gbw3td2mlw7exlwmerkvw` (`rareaName`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =2
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `osType`
--

CREATE TABLE IF NOT EXISTS `osType` (
  `id`           BIGINT(20) NOT NULL AUTO_INCREMENT,
  `architecture` VARCHAR(255) DEFAULT NULL,
  `osTypeName`   VARCHAR(255) DEFAULT NULL,
  `version`      INT(11) DEFAULT NULL,
  `company_id`   BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_crf1osblyu69u2qm2wqjjf24p` (`osTypeName`),
  KEY `FK_28swxpviq8cw0hd8wnnxnt5kq` (`company_id`),
  CONSTRAINT `FK_28swxpviq8cw0hd8wnnxnt5kq` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =2
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `team`
--

CREATE TABLE IF NOT EXISTS `team` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `teamCC`      VARCHAR(255) DEFAULT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `teamName`    VARCHAR(255) DEFAULT NULL,
  `version`     INT(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2ip36hu2iwi68lo7dvvimf7h5` (`teamName`, `teamCC`),
  UNIQUE KEY `UK_92vjfp1qloqgn9erju73tui17` (`teamCC`),
  UNIQUE KEY `UK_68alxs66iye9swtlq7ie8hqyl` (`teamName`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =6
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `osInstance`
--

CREATE TABLE IF NOT EXISTS `osInstance` (
  `id`                     BIGINT(20) NOT NULL AUTO_INCREMENT,
  `adminGateURI`           VARCHAR(255) DEFAULT NULL,
  `description`            VARCHAR(255) DEFAULT NULL,
  `osName`                 VARCHAR(255) DEFAULT NULL,
  `version`                INT(11) DEFAULT NULL,
  `embeddingOSInstance_id` BIGINT(20) DEFAULT NULL,
  `osType_id`              BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hi1vhidn8t8b69gk8fblps1lk` (`osName`),
  KEY `FK_atplleu6rvhpn7d5em808p6wk` (`embeddingOSInstance_id`),
  KEY `FK_3xe2sji9js08isqnsini28ova` (`osType_id`),
  CONSTRAINT `FK_3xe2sji9js08isqnsini28ova` FOREIGN KEY (`osType_id`) REFERENCES `osType` (`id`),
  CONSTRAINT `FK_atplleu6rvhpn7d5em808p6wk` FOREIGN KEY (`embeddingOSInstance_id`) REFERENCES `osInstance` (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =6
  DEFAULT CHARSET =latin1;

--
-- Table structure for table `subnet`
--

CREATE TABLE IF NOT EXISTS `subnet` (
  `id`          BIGINT(20) NOT NULL AUTO_INCREMENT,
  `description` VARCHAR(255) DEFAULT NULL,
  `subnetName`  VARCHAR(255) DEFAULT NULL,
  `subnetIP`    VARCHAR(255) DEFAULT NULL,
  `subnetMask`  VARCHAR(255) DEFAULT NULL,
  `version`     INT(11) DEFAULT NULL,
  `rarea_id`    BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2o6k6apvpj6ruxg8j9pi9s62d` (`subnetName`),
  KEY `FK_6kwi7i739mnpcpreb2qrhh7s5` (`rarea_id`),
  CONSTRAINT `FK_6kwi7i739mnpcpreb2qrhh7s5` FOREIGN KEY (`rarea_id`) REFERENCES `routingArea` (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =4
  DEFAULT CHARSET =latin1;

--
--  Table structure for table `ipAddress`
--

CREATE TABLE IF NOT EXISTS `ipaddress` (
  `id`               BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `fqdn`             VARCHAR(255) DEFAULT NULL,
  `ipAddress`        VARCHAR(255) NOT NULL,
  `version`          INT(11) DEFAULT NULL,
  `networkSubnet_id` BIGINT(20)   NOT NULL,
  `osInstance_id`    BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_2b6m0akrc8lfui9ed2v9s32va` (`fqdn`),
  KEY `FK_2rykbmwp9s1qxwtkr223n3xr7` (`osInstance_id`),
  KEY `FK_3n0207eaccxw6baxbsonaqtl6` (`networkSubnet_id`),
  CONSTRAINT `FK_2rykbmwp9s1qxwtkr223n3xr7` FOREIGN KEY (`osInstance_id`) REFERENCES `osInstance` (`id`),
  CONSTRAINT `FK_3n0207eaccxw6baxbsonaqtl6` FOREIGN KEY (`networkSubnet_id`) REFERENCES `subnet` (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;

--
--  Table structure for table `niCard`
--

CREATE TABLE IF NOT EXISTS `niCard` (
  `id`             BIGINT(20)   NOT NULL AUTO_INCREMENT,
  `duplex`         VARCHAR(255) DEFAULT NULL,
  `macAddress`     VARCHAR(255) NOT NULL,
  `mtu`            INT(11) DEFAULT NULL,
  `name`           VARCHAR(255) DEFAULT NULL,
  `speed`          INT(11) DEFAULT NULL,
  `version`        INT(11) DEFAULT NULL,
  `ripAddress_id`  BIGINT(20) DEFAULT NULL,
  `rosInstance_id` BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_1491al4gj9v1f25y15kryfer7` (`macAddress`),
  KEY `FK_mgeuv29q2f7upop0lq2jma1fv` (`ripAddress_id`),
  KEY `FK_2pwy48wp8jek3p8yr46kt905u` (`rosInstance_id`),
  CONSTRAINT `FK_2pwy48wp8jek3p8yr46kt905u` FOREIGN KEY (`rosInstance_id`) REFERENCES `osInstance` (`id`),
  CONSTRAINT `FK_mgeuv29q2f7upop0lq2jma1fv` FOREIGN KEY (`ripAddress_id`) REFERENCES `ipaddress` (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =5
  DEFAULT CHARSET =latin1;

--
-- Table structure for table `application`
--

CREATE TABLE IF NOT EXISTS `application` (
  `id`              BIGINT(20) NOT NULL AUTO_INCREMENT,
  `applicationCC`   VARCHAR(255) DEFAULT NULL,
  `description`     VARCHAR(255) DEFAULT NULL,
  `applicationName` VARCHAR(255) DEFAULT NULL,
  `shortName`       VARCHAR(255) DEFAULT NULL,
  `version`         INT(11) DEFAULT NULL,
  `company_id`      BIGINT(20) DEFAULT NULL,
  `team_id`         BIGINT(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_8p01ijg8n5dswqhrblmqr4axr` (`applicationName`, `applicationCC`),
  UNIQUE KEY `UK_5j7h3w1yqbm5gnw0g3ws30txm` (`applicationCC`),
  UNIQUE KEY `UK_77mi6s1lve4uo7qp9dj0eoeta` (`applicationName`),
  KEY `FK_nscwgna6gy8j8uicip6s572a3` (`company_id`),
  KEY `FK_i1txg9ron2idn45hk3c0i194b` (`team_id`),
  CONSTRAINT `FK_i1txg9ron2idn45hk3c0i194b` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`),
  CONSTRAINT `FK_nscwgna6gy8j8uicip6s572a3` FOREIGN KEY (`company_id`) REFERENCES `company` (`id`)
)
  ENGINE =InnoDB
  AUTO_INCREMENT =6
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `application_osInstance`
--

CREATE TABLE IF NOT EXISTS `application_osInstance` (
  `applications_id` BIGINT(20) NOT NULL,
  `osInstances_id`  BIGINT(20) NOT NULL,
  PRIMARY KEY (`applications_id`, `osInstances_id`),
  KEY `FK_61ggwwol8wsjns994me747t3l` (`osInstances_id`),
  CONSTRAINT `FK_dym8cvgfyfqr3pno7676evm3s` FOREIGN KEY (`applications_id`) REFERENCES `application` (`id`),
  CONSTRAINT `FK_61ggwwol8wsjns994me747t3l` FOREIGN KEY (`osInstances_id`) REFERENCES `osInstance` (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `location_multicastArea`
--

CREATE TABLE IF NOT EXISTS `location_routingArea` (
  `locations_id`    BIGINT(20) NOT NULL,
  `routingAreas_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`locations_id`, `routingAreas_id`),
  KEY `FK_27c15suabg8yab15vn5l9k3as` (`routingAreas_id`),
  CONSTRAINT `FK_5pxtweidw0t7dkmipbqn5cg2m` FOREIGN KEY (`locations_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FK_27c15suabg8yab15vn5l9k3as` FOREIGN KEY (`routingAreas_id`) REFERENCES `routingArea` (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;

--
-- Table structure for table `location_subnet`
--

CREATE TABLE IF NOT EXISTS `location_subnet` (
  `locations_id` BIGINT(20) NOT NULL,
  `subnets_id`   BIGINT(20) NOT NULL,
  PRIMARY KEY (`locations_id`, `subnets_id`),
  KEY `FK_iqpmexlnja85dr94poer8yb68` (`subnets_id`),
  CONSTRAINT `FK_1oxl0gc930d0rms4vsgsd8nny` FOREIGN KEY (`locations_id`) REFERENCES `location` (`id`),
  CONSTRAINT `FK_iqpmexlnja85dr94poer8yb68` FOREIGN KEY (`subnets_id`) REFERENCES `subnet` (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `environment_osInstance`
--

CREATE TABLE IF NOT EXISTS `environment_osInstance` (
  `environments_id` BIGINT(20) NOT NULL,
  `osInstances_id`  BIGINT(20) NOT NULL,
  PRIMARY KEY (`environments_id`, `osInstances_id`),
  KEY `FK_cxvpw4agvkxa999jx75wjg3qj` (`osInstances_id`),
  CONSTRAINT `FK_5n70i7gg5g8u7spgqq9227xw` FOREIGN KEY (`environments_id`) REFERENCES `environment` (`id`),
  CONSTRAINT `FK_cxvpw4agvkxa999jx75wjg3qj` FOREIGN KEY (`osInstances_id`) REFERENCES `osInstance` (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `subnet_osInstance`
--

CREATE TABLE IF NOT EXISTS `subnet_osInstance` (
  `networkSubnets_id` BIGINT(20) NOT NULL,
  `osInstances_id`    BIGINT(20) NOT NULL,
  PRIMARY KEY (`networkSubnets_id`, `osInstances_id`),
  KEY `FK_lndikru7geu52u0wmqwbgk4jj` (`osInstances_id`),
  CONSTRAINT `FK_foupufssnqdx41vt4ry90d070` FOREIGN KEY (`networkSubnets_id`) REFERENCES `subnet` (`id`),
  CONSTRAINT `FK_lndikru7geu52u0wmqwbgk4jj` FOREIGN KEY (`osInstances_id`) REFERENCES `osInstance` (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;


--
-- Table structure for table `team_osInstance`
--

CREATE TABLE IF NOT EXISTS `team_osInstance` (
  `teams_id`       BIGINT(20) NOT NULL,
  `osInstances_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`teams_id`, `osInstances_id`),
  KEY `FK_4bcdvjduli9spkl0m1q26cwr1` (`osInstances_id`),
  CONSTRAINT `FK_cc24sbfxytin2iajw2lggn65m` FOREIGN KEY (`teams_id`) REFERENCES `team` (`id`),
  CONSTRAINT `FK_4bcdvjduli9spkl0m1q26cwr1` FOREIGN KEY (`osInstances_id`) REFERENCES `osInstance` (`id`)
)
  ENGINE =InnoDB
  DEFAULT CHARSET =latin1;