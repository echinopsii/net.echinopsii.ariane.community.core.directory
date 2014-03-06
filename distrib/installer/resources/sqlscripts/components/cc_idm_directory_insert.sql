--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
INSERT INTO `resource` VALUES
    (7,'CC directory common IT network datacenter','ccDirComITiNtwDC',6),
    (8,'CC directory common IT network multicast area','ccDirComITiNtwMarea',4),
    (9,'CC directory common IT network subnet','ccDirComITiNtwSubnet',4),
    (10,'CC directory common IT system OS instance','ccDirComITiSysOsi',4),
    (11,'CC directory common IT system OS type','ccDirComITiSysOst',4),
    (12,'CC directory common organisation application','ccDirComOrgApp',4),
    (13,'CC directory common organisation company','ccDirComOrgCompany',4),
    (14,'CC directory common organisation environment','ccDirComOrgEnvironment',4),
    (15,'CC directory common organisation team','ccDirComOrgTeam',4);
UNLOCK TABLES;



--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE;
INSERT INTO `permission` VALUES
    (23,'can display CC directory common IT network datacenter','ccDirComITiNtwDC:display',5,7),
    (24,'can create CC directory common IT network datacenter','ccDirComITiNtwDC:create',6,7),
    (25,'can remove CC directory common IT network datacenter','ccDirComITiNtwDC:remove',4,7),
    (26,'can update CC directory common IT network datacenter','ccDirComITiNtwDC:update',4,7),
    (27,'can display CC directory common IT network multicast area','ccDirComITiNtwMarea:display',5,8),
    (28,'can create CC directory common IT network multicast area','ccDirComITiNtwMarea:create',4,8),
    (29,'can remove CC directory common IT network multicast area','ccDirComITiNtwMarea:remove',4,8),
    (30,'can update CC directory common IT network multicast area','ccDirComITiNtwMarea:update',4,8),
    (31,'can display CC directory common IT network subnet','ccDirComITiNtwSubnet:display',6,9),
    (32,'can create CC directory common IT network subnet','ccDirComITiNtwSubnet:create',4,9),
    (33,'can remove CC directory common IT network subnet','ccDirComITiNtwSubnet:remove',4,9),
    (34,'can update CC directory common IT network subnet','ccDirComITiNtwSubnet:update',2,9),
    (35,'can display CC directory common IT system OS instance','ccDirComITiSysOsi:display',6,10),
    (36,'can create CC directory common IT system OS instance','ccDirComITiSysOsi:create',6,10),
    (37,'can remove CC directory common IT system OS instance','ccDirComITiSysOsi:remove',4,10),
    (38,'can update CC directory common IT system OS instance','ccDirComITiSysOsi:update',4,10),
    (39,'can create CC directory common IT system OS type','ccDirComITiSysOst:create',4,11),
    (40,'can display CC directory common IT system OS type','ccDirComITiSysOst:display',5,11),
    (41,'can remove CC directory common IT system OS type','ccDirComITiSysOst:remove',4,11),
    (42,'can update CC directory common IT system OS type','ccDirComITiSysOst:update',4,11),
    (43,'can display CC directory common organisation application','ccDirComOrgApp:display',5,12),
    (44,'can create CC directory common organisation application','ccDirComOrgApp:create',4,12),
    (45,'can remove CC directory common organisation application','ccDirComOrgApp:remove',4,12),
    (46,'can update CC directory common organisation application','ccDirComOrgApp:update',4,12),
    (47,'can display CC directory common organisation company','ccDirComOrgCompany:display',5,13),
    (48,'can create CC directory common organisation company','ccDirComOrgCompany:create',4,13),
    (49,'can remove CC directory common organisation company','ccDirComOrgCompany:remove',4,13),
    (50,'can update CC directory common organisation company','ccDirComOrgCompany:update',4,13),
    (51,'can display CC directory common organisation environment','ccDirComOrgEnvironment:display',5,14),
    (52,'can create CC directory common organisation environment','ccDirComOrgEnvironment:create',4,14),
    (53,'can remove CC directory common organisation environment','ccDirComOrgEnvironment:remove',2,14),
    (54,'can update CC directory common organisation environment','ccDirComOrgEnvironment:update',2,14),
    (55,'can display CC directory common organisation team','ccDirComOrgTeam:display',3,15),
    (56,'can create CC directory common organisation team','ccDirComOrgTeam:create',2,15),
    (57,'can remove CC directory common organisation team','ccDirComOrgTeam:remove',2,15),
    (58,'can update CC directory common organisation team','ccDirComOrgTeam:update',2,15);
UNLOCK TABLES;



--
-- Dumping data for table `resource_permission`
--

LOCK TABLES `resource_permission` WRITE;
INSERT INTO `resource_permission` VALUES
    (7,23),(7,24),(7,25),(7,26),
    (8,27),(8,28),(8,29),(8,30),
    (9,31),(9,32),(9,33),(9,34),
    (10,35),(10,36),(10,37),(10,38),
    (11,39),(11,40),(11,41),(11,42),
    (12,43),(12,44),(12,45),(12,46),
    (13,47),(13,48),(13,49),(13,50),
    (14,51),(14,52),(14,53),(14,54),
    (15,55),(15,56),(15,57),(15,58);
UNLOCK TABLES;



--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
INSERT INTO `role` VALUES
    (7,'CC network administrator role','ccntwadmin',11),
    (8,'CC system administrator role','ccsysadmin',7),
    (9,'CC organisation administrator role','ccorgadmin',5),
    (13,'CC network reviewer role','ccntwreviewer',11),
    (14,'CC system reviewer role','ccsysreviewer',7),
    (15,'CC organisation reviewer role','ccorgreviewer',7);
UNLOCK TABLES;



--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE;
INSERT INTO `permission_role` VALUES
    (23,7),(23,1),(23,13),
    (24,7),(24,1),
    (25,7),(25,1),
    (26,7),(26,1),
    (27,7),(27,1),(27,13),
    (28,7),(28,1),
    (29,7),(29,1),
    (30,7),(30,1),
    (31,7),(31,1),(31,13),
    (32,7),(32,1),
    (33,7),(33,1),
    (34,7),(34,1),
    (35,8),(35,1),(35,14),
    (36,8),(36,1),
    (37,8),(37,1),
    (38,8),(38,1),
    (39,8),(39,1),
    (40,8),(40,1),(40,14),
    (41,8),(41,1),
    (42,8),(42,1),
    (43,9),(43,1),(43,15),
    (44,9),(44,1),
    (45,9),(45,1),
    (46,9),(46,1),
    (47,9),(47,1),(47,15),
    (48,9),(48,1),
    (49,9),(49,1),
    (50,9),(50,1),
    (51,9),(51,1),(51,15),
    (52,9),(52,1),
    (53,9),(53,1),
    (54,9),(54,1),
    (55,9),(55,1),(55,15),
    (56,9),(56,1),
    (57,9),(57,1),
    (58,9),(58,1);
UNLOCK TABLES;



--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
INSERT INTO `role_permission` VALUES
    (1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32),(1,33),(1,34),
    (1,35),(1,36),(1,37),(1,38),(1,39),(1,40),(1,41),(1,42),(1,43),(1,44),(1,45),(1,46),
    (1,47),(1,48),(1,49),(1,50),(1,51),(1,52),(1,53),(1,54),(1,55),(1,56),(1,57),(1,58),
    (7,23),(7,24),(7,25),(7,26),(7,27),(7,28),(7,29),(7,30),(7,31),(7,32),(7,33),(7,34),
    (8,35),(8,36),(8,37),(8,38),(8,39),(8,40),(8,41),(8,42),
    (9,43),(9,44),(9,45),(9,46),(9,47),(9,48),(9,49),(9,50),(9,51),(9,52),(9,53),(9,54),(9,55),(9,56),(9,57),(9,58),
    (13,23),(13,27),(13,31),
    (14,35),(14,40),
    (15,43),(15,47),(15,51),(15,55);
UNLOCK TABLES;