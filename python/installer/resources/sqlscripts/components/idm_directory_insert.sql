--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
INSERT IGNORE INTO `resource` (description, resourceName, version) VALUES
    ('CC directory common IT network datacenter','ccDirComITiNtwDC',1),
    ('CC directory common IT network multicast area','ccDirComITiNtwMarea',1),
    ('CC directory common IT network subnet','ccDirComITiNtwSubnet',1),
    ('CC directory common IT system OS instance','ccDirComITiSysOsi',1),
    ('CC directory common IT system OS type','ccDirComITiSysOst',1),
    ('CC directory common organisation application','ccDirComOrgApp',1),
    ('CC directory common organisation company','ccDirComOrgCompany',1),
    ('CC directory common organisation environment','ccDirComOrgEnvironment',1),
    ('CC directory common organisation team','ccDirComOrgTeam',1);
UNLOCK TABLES;



--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE,`resource` WRITE;
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common IT network datacenter', 'ccDirComITiNtwDC:display', 1, id FROM resource WHERE resourceName='ccDirComITiNtwDC';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common IT network datacenter', 'ccDirComITiNtwDC:create', 1, id FROM resource WHERE resourceName='ccDirComITiNtwDC';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common IT network datacenter', 'ccDirComITiNtwDC:remove', 1, id FROM resource WHERE resourceName='ccDirComITiNtwDC';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common IT network datacenter', 'ccDirComITiNtwDC:update', 1, id FROM resource WHERE resourceName='ccDirComITiNtwDC';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common IT network multicast area', 'ccDirComITiNtwMarea:display', 1, id FROM resource WHERE resourceName='ccDirComITiNtwMarea';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common IT network multicast area', 'ccDirComITiNtwMarea:create', 1, id FROM resource WHERE resourceName='ccDirComITiNtwMarea';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common IT network multicast area', 'ccDirComITiNtwMarea:remove', 1, id FROM resource WHERE resourceName='ccDirComITiNtwMarea';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common IT network multicast area', 'ccDirComITiNtwMarea:update', 1, id FROM resource WHERE resourceName='ccDirComITiNtwMarea';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common IT network subnet', 'ccDirComITiNtwSubnet:display', 1, id FROM resource WHERE resourceName='ccDirComITiNtwSubnet';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common IT network subnet', 'ccDirComITiNtwSubnet:create', 1, id FROM resource WHERE resourceName='ccDirComITiNtwSubnet';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common IT network subnet', 'ccDirComITiNtwSubnet:remove', 1, id FROM resource WHERE resourceName='ccDirComITiNtwSubnet';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common IT network subnet', 'ccDirComITiNtwSubnet:update', 1, id FROM resource WHERE resourceName='ccDirComITiNtwSubnet';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common IT system OS instance', 'ccDirComITiSysOsi:display', 1, id FROM resource WHERE resourceName='ccDirComITiSysOsi';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common IT system OS instance', 'ccDirComITiSysOsi:create', 1, id FROM resource WHERE resourceName='ccDirComITiSysOsi';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common IT system OS instance', 'ccDirComITiSysOsi:remove', 1, id FROM resource WHERE resourceName='ccDirComITiSysOsi';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common IT system OS instance', 'ccDirComITiSysOsi:update', 1, id FROM resource WHERE resourceName='ccDirComITiSysOsi';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common IT system OS type', 'ccDirComITiSysOst:display', 1, id FROM resource WHERE resourceName='ccDirComITiSysOst';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common IT system OS type', 'ccDirComITiSysOst:create', 1, id FROM resource WHERE resourceName='ccDirComITiSysOst';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common IT system OS type', 'ccDirComITiSysOst:remove', 1, id FROM resource WHERE resourceName='ccDirComITiSysOst';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common IT system OS type', 'ccDirComITiSysOst:update', 1, id FROM resource WHERE resourceName='ccDirComITiSysOst';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common organisation application', 'ccDirComOrgApp:display', 1, id FROM resource WHERE resourceName='ccDirComOrgApp';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common organisation application', 'ccDirComOrgApp:create', 1, id FROM resource WHERE resourceName='ccDirComOrgApp';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common organisation application', 'ccDirComOrgApp:remove', 1, id FROM resource WHERE resourceName='ccDirComOrgApp';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common organisation application', 'ccDirComOrgApp:update', 1, id FROM resource WHERE resourceName='ccDirComOrgApp';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common organisation company', 'ccDirComOrgCompany:display', 1, id FROM resource WHERE resourceName='ccDirComOrgCompany';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common organisation company', 'ccDirComOrgCompany:create', 1, id FROM resource WHERE resourceName='ccDirComOrgCompany';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common organisation company', 'ccDirComOrgCompany:remove', 1, id FROM resource WHERE resourceName='ccDirComOrgCompany';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common organisation company', 'ccDirComOrgCompany:update', 1, id FROM resource WHERE resourceName='ccDirComOrgCompany';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common organisation environment', 'ccDirComOrgEnvironment:display', 1, id FROM resource WHERE resourceName='ccDirComOrgEnvironment';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common organisation environment', 'ccDirComOrgEnvironment:create', 1, id FROM resource WHERE resourceName='ccDirComOrgEnvironment';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common organisation environment', 'ccDirComOrgEnvironment:remove', 1, id FROM resource WHERE resourceName='ccDirComOrgEnvironment';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common organisation environment', 'ccDirComOrgEnvironment:update', 1, id FROM resource WHERE resourceName='ccDirComOrgEnvironment';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display CC directory common organisation team', 'ccDirComOrgTeam:display', 1, id FROM resource WHERE resourceName='ccDirComOrgTeam';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create CC directory common organisation team', 'ccDirComOrgTeam:create', 1, id FROM resource WHERE resourceName='ccDirComOrgTeam';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove CC directory common organisation team', 'ccDirComOrgTeam:remove', 1, id FROM resource WHERE resourceName='ccDirComOrgTeam';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update CC directory common organisation team', 'ccDirComOrgTeam:update', 1, id FROM resource WHERE resourceName='ccDirComOrgTeam';

UNLOCK TABLES;



--
-- Dumping data for table `resource_permission`
--

LOCK TABLES `resource_permission` WRITE,`permission` AS p WRITE,`resource` AS r WRITE ;
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwDC' AND p.permissionName='ccDirComITiNtwDC:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwDC' AND p.permissionName='ccDirComITiNtwDC:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwDC' AND p.permissionName='ccDirComITiNtwDC:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwDC' AND p.permissionName='ccDirComITiNtwDC:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwMarea' AND p.permissionName='ccDirComITiNtwMarea:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwMarea' AND p.permissionName='ccDirComITiNtwMarea:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwMarea' AND p.permissionName='ccDirComITiNtwMarea:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwMarea' AND p.permissionName='ccDirComITiNtwMarea:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwSubnet' AND p.permissionName='ccDirComITiNtwSubnet:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwSubnet' AND p.permissionName='ccDirComITiNtwSubnet:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwSubnet' AND p.permissionName='ccDirComITiNtwSubnet:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiNtwSubnet' AND p.permissionName='ccDirComITiNtwSubnet:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOsi' AND p.permissionName='ccDirComITiSysOsi:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOsi' AND p.permissionName='ccDirComITiSysOsi:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOsi' AND p.permissionName='ccDirComITiSysOsi:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOsi' AND p.permissionName='ccDirComITiSysOsi:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOst' AND p.permissionName='ccDirComITiSysOst:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOst' AND p.permissionName='ccDirComITiSysOst:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOst' AND p.permissionName='ccDirComITiSysOst:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComITiSysOst' AND p.permissionName='ccDirComITiSysOst:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgApp' AND p.permissionName='ccDirComOrgApp:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgApp' AND p.permissionName='ccDirComOrgApp:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgApp' AND p.permissionName='ccDirComOrgApp:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgApp' AND p.permissionName='ccDirComOrgApp:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgCompany' AND p.permissionName='ccDirComOrgCompany:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgCompany' AND p.permissionName='ccDirComOrgCompany:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgCompany' AND p.permissionName='ccDirComOrgCompany:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgCompany' AND p.permissionName='ccDirComOrgCompany:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgEnvironment' AND p.permissionName='ccDirComOrgEnvironment:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgEnvironment' AND p.permissionName='ccDirComOrgEnvironment:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgEnvironment' AND p.permissionName='ccDirComOrgEnvironment:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgEnvironment' AND p.permissionName='ccDirComOrgEnvironment:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgTeam' AND p.permissionName='ccDirComOrgTeam:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgTeam' AND p.permissionName='ccDirComOrgTeam:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgTeam' AND p.permissionName='ccDirComOrgTeam:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='ccDirComOrgTeam' AND p.permissionName='ccDirComOrgTeam:update';

UNLOCK TABLES;



--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
INSERT IGNORE INTO `role` (description, roleName, version) VALUES
    ('CC network administrator role','ccntwadmin',1),
    ('CC system administrator role','ccsysadmin',1),
    ('CC organisation administrator role','ccorgadmin',1),
    ('CC network reviewer role','ccntwreviewer',1),
    ('CC system reviewer role','ccsysreviewer',1),
    ('CC organisation reviewer role','ccorgreviewer',1);
UNLOCK TABLES;



--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE,`permission` AS p WRITE,`role` AS r WRITE;
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:update' AND r.roleName='ccntwadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:update' AND r.roleName='ccntwadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:update' AND r.roleName='ccntwadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:display' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:display' AND r.roleName='ccsysreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:create' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:create' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:update' AND r.roleName='ccsysadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:display' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:display' AND r.roleName='ccsysreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:create' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:create' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:update' AND r.roleName='ccsysadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:update' AND r.roleName='ccorgadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:update' AND r.roleName='ccorgadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:update' AND r.roleName='ccorgadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:update' AND r.roleName='ccorgadmin';

UNLOCK TABLES;



--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE,`permission` AS p WRITE,`role` AS r WRITE;

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:update' AND r.roleName='Jedi';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:remove' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:update' AND r.roleName='ccntwadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:remove' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:update' AND r.roleName='ccntwadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:display' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:create' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:remove' AND r.roleName='ccntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:update' AND r.roleName='ccntwadmin';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwDC:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwMarea:display' AND r.roleName='ccntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiNtwSubnet:display' AND r.roleName='ccntwreviewer';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:display' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:create' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:remove' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:update' AND r.roleName='ccsysadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:display' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:create' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:remove' AND r.roleName='ccsysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:update' AND r.roleName='ccsysadmin';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOsi:display' AND r.roleName='ccsysreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComITiSysOst:display' AND r.roleName='ccsysreviewer';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:remove' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:update' AND r.roleName='ccorgadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:remove' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:update' AND r.roleName='ccorgadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:remove' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:update' AND r.roleName='ccorgadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:display' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:create' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:remove' AND r.roleName='ccorgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:update' AND r.roleName='ccorgadmin';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgApp:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgCompany:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgEnvironment:display' AND r.roleName='ccorgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='ccDirComOrgTeam:display' AND r.roleName='ccorgreviewer';
UNLOCK TABLES;