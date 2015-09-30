--
-- Dumping data for table `resource`
--

LOCK TABLES `resource` WRITE;
INSERT IGNORE INTO `resource` (description, resourceName, version) VALUES
  ('Directory common IT network routing area','dirComITiNtwRarea',1),
  ('Directory common IT network location','dirComITiNtwDC',1),
    ('Directory common IT network subnet','dirComITiNtwSubnet',1),
    ('Directory common IT network ipaddress','dirComITiNtwIPAddress',1),
    ('Directory common IT network interface card','dirComITiNtwNICard',1),
    ('Directory common IT system OS instance','dirComITiSysOsi',1),
    ('Directory common IT system OS type','dirComITiSysOst',1),
    ('Directory common organisation application','dirComOrgApp',1),
    ('Directory common organisation company','dirComOrgCompany',1),
    ('Directory common organisation environment','dirComOrgEnvironment',1),
    ('Directory common organisation team','dirComOrgTeam',1);
UNLOCK TABLES;



--
-- Dumping data for table `permission`
--

LOCK TABLES `permission` WRITE,`resource` WRITE;
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common IT network location', 'dirComITiNtwDC:display', 1, id FROM resource WHERE resourceName='dirComITiNtwDC';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common IT network location', 'dirComITiNtwDC:create', 1, id FROM resource WHERE resourceName='dirComITiNtwDC';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common IT network location', 'dirComITiNtwDC:remove', 1, id FROM resource WHERE resourceName='dirComITiNtwDC';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common IT network location', 'dirComITiNtwDC:update', 1, id FROM resource WHERE resourceName='dirComITiNtwDC';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common IT network routing area', 'dirComITiNtwRarea:display', 1, id FROM resource WHERE resourceName='dirComITiNtwRarea';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common IT network routing area', 'dirComITiNtwRarea:create', 1, id FROM resource WHERE resourceName='dirComITiNtwRarea';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common IT network routing area', 'dirComITiNtwRarea:remove', 1, id FROM resource WHERE resourceName='dirComITiNtwRarea';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common IT network routing area', 'dirComITiNtwRarea:update', 1, id FROM resource WHERE resourceName='dirComITiNtwRarea';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common IT network subnet', 'dirComITiNtwSubnet:display', 1, id FROM resource WHERE resourceName='dirComITiNtwSubnet';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common IT network subnet', 'dirComITiNtwSubnet:create', 1, id FROM resource WHERE resourceName='dirComITiNtwSubnet';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common IT network subnet', 'dirComITiNtwSubnet:remove', 1, id FROM resource WHERE resourceName='dirComITiNtwSubnet';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common IT network subnet', 'dirComITiNtwSubnet:update', 1, id FROM resource WHERE resourceName='dirComITiNtwSubnet';


INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common IT network ipaddress', 'dirComITiNtwIPAddress:display', 1, id FROM resource WHERE resourceName='dirComITiNtwIPAddress';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common IT network ipaddress', 'dirComITiNtwIPAddress:create', 1, id FROM resource WHERE resourceName='dirComITiNtwIPAddress';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common IT network ipaddress', 'dirComITiNtwIPAddress:remove', 1, id FROM resource WHERE resourceName='dirComITiNtwIPAddress';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common IT network ipaddress', 'dirComITiNtwIPAddress:update', 1, id FROM resource WHERE resourceName='dirComITiNtwIPAddress';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common IT network interface card', 'dirComITiNtwNICard:display', 1, id FROM resource WHERE resourceName='dirComITiNtwNICard';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common IT network interface card', 'dirComITiNtwNICard:create', 1, id FROM resource WHERE resourceName='dirComITiNtwNICard';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common IT network interface card', 'dirComITiNtwNICard:remove', 1, id FROM resource WHERE resourceName='dirComITiNtwNICard';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common IT network interface card', 'dirComITiNtwNICard:update', 1, id FROM resource WHERE resourceName='dirComITiNtwNICard';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common IT system OS instance', 'dirComITiSysOsi:display', 1, id FROM resource WHERE resourceName='dirComITiSysOsi';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common IT system OS instance', 'dirComITiSysOsi:create', 1, id FROM resource WHERE resourceName='dirComITiSysOsi';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common IT system OS instance', 'dirComITiSysOsi:remove', 1, id FROM resource WHERE resourceName='dirComITiSysOsi';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common IT system OS instance', 'dirComITiSysOsi:update', 1, id FROM resource WHERE resourceName='dirComITiSysOsi';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common IT system OS type', 'dirComITiSysOst:display', 1, id FROM resource WHERE resourceName='dirComITiSysOst';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common IT system OS type', 'dirComITiSysOst:create', 1, id FROM resource WHERE resourceName='dirComITiSysOst';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common IT system OS type', 'dirComITiSysOst:remove', 1, id FROM resource WHERE resourceName='dirComITiSysOst';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common IT system OS type', 'dirComITiSysOst:update', 1, id FROM resource WHERE resourceName='dirComITiSysOst';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common organisation application', 'dirComOrgApp:display', 1, id FROM resource WHERE resourceName='dirComOrgApp';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common organisation application', 'dirComOrgApp:create', 1, id FROM resource WHERE resourceName='dirComOrgApp';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common organisation application', 'dirComOrgApp:remove', 1, id FROM resource WHERE resourceName='dirComOrgApp';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common organisation application', 'dirComOrgApp:update', 1, id FROM resource WHERE resourceName='dirComOrgApp';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common organisation company', 'dirComOrgCompany:display', 1, id FROM resource WHERE resourceName='dirComOrgCompany';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common organisation company', 'dirComOrgCompany:create', 1, id FROM resource WHERE resourceName='dirComOrgCompany';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common organisation company', 'dirComOrgCompany:remove', 1, id FROM resource WHERE resourceName='dirComOrgCompany';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common organisation company', 'dirComOrgCompany:update', 1, id FROM resource WHERE resourceName='dirComOrgCompany';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common organisation environment', 'dirComOrgEnvironment:display', 1, id FROM resource WHERE resourceName='dirComOrgEnvironment';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common organisation environment', 'dirComOrgEnvironment:create', 1, id FROM resource WHERE resourceName='dirComOrgEnvironment';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common organisation environment', 'dirComOrgEnvironment:remove', 1, id FROM resource WHERE resourceName='dirComOrgEnvironment';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common organisation environment', 'dirComOrgEnvironment:update', 1, id FROM resource WHERE resourceName='dirComOrgEnvironment';

INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can display Directory common organisation team', 'dirComOrgTeam:display', 1, id FROM resource WHERE resourceName='dirComOrgTeam';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can create Directory common organisation team', 'dirComOrgTeam:create', 1, id FROM resource WHERE resourceName='dirComOrgTeam';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can remove Directory common organisation team', 'dirComOrgTeam:remove', 1, id FROM resource WHERE resourceName='dirComOrgTeam';
INSERT IGNORE INTO `permission` (description, permissionName, version, resource_id)
SELECT 'can update Directory common organisation team', 'dirComOrgTeam:update', 1, id FROM resource WHERE resourceName='dirComOrgTeam';

UNLOCK TABLES;



--
-- Dumping data for table `resource_permission`
--

LOCK TABLES `resource_permission` WRITE,`permission` AS p WRITE,`resource` AS r WRITE ;
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwDC' AND p.permissionName='dirComITiNtwDC:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwDC' AND p.permissionName='dirComITiNtwDC:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwDC' AND p.permissionName='dirComITiNtwDC:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwDC' AND p.permissionName='dirComITiNtwDC:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwRarea' AND p.permissionName='dirComITiNtwRarea:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwRarea' AND p.permissionName='dirComITiNtwRarea:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwRarea' AND p.permissionName='dirComITiNtwRarea:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwRarea' AND p.permissionName='dirComITiNtwRarea:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwSubnet' AND p.permissionName='dirComITiNtwSubnet:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwSubnet' AND p.permissionName='dirComITiNtwSubnet:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwSubnet' AND p.permissionName='dirComITiNtwSubnet:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwSubnet' AND p.permissionName='dirComITiNtwSubnet:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwIPAddress' AND p.permissionName='dirComITiNtwIPAddress:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwIPAddress' AND p.permissionName='dirComITiNtwIPAddress:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwIPAddress' AND p.permissionName='dirComITiNtwIPAddress:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwIPAddress' AND p.permissionName='dirComITiNtwIPAddress:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwNICard' AND p.permissionName='dirComITiNtwNICard:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwNICard' AND p.permissionName='dirComITiNtwNICard:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwNICard' AND p.permissionName='dirComITiNtwNICard:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiNtwNICard' AND p.permissionName='dirComITiNtwNICard:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOsi' AND p.permissionName='dirComITiSysOsi:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOsi' AND p.permissionName='dirComITiSysOsi:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOsi' AND p.permissionName='dirComITiSysOsi:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOsi' AND p.permissionName='dirComITiSysOsi:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOst' AND p.permissionName='dirComITiSysOst:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOst' AND p.permissionName='dirComITiSysOst:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOst' AND p.permissionName='dirComITiSysOst:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComITiSysOst' AND p.permissionName='dirComITiSysOst:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgApp' AND p.permissionName='dirComOrgApp:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgApp' AND p.permissionName='dirComOrgApp:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgApp' AND p.permissionName='dirComOrgApp:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgApp' AND p.permissionName='dirComOrgApp:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgCompany' AND p.permissionName='dirComOrgCompany:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgCompany' AND p.permissionName='dirComOrgCompany:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgCompany' AND p.permissionName='dirComOrgCompany:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgCompany' AND p.permissionName='dirComOrgCompany:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgEnvironment' AND p.permissionName='dirComOrgEnvironment:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgEnvironment' AND p.permissionName='dirComOrgEnvironment:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgEnvironment' AND p.permissionName='dirComOrgEnvironment:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgEnvironment' AND p.permissionName='dirComOrgEnvironment:update';

INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgTeam' AND p.permissionName='dirComOrgTeam:display';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgTeam' AND p.permissionName='dirComOrgTeam:create';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgTeam' AND p.permissionName='dirComOrgTeam:remove';
INSERT IGNORE INTO `resource_permission` (resource_id, permissions_id)
SELECT r.id, p.id FROM resource AS r, permission AS p WHERE r.resourceName='dirComOrgTeam' AND p.permissionName='dirComOrgTeam:update';

UNLOCK TABLES;



--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
INSERT IGNORE INTO `role` (description, roleName, version) VALUES
    ('network administrator role','ntwadmin',1),
    ('system administrator role','sysadmin',1),
    ('organisation administrator role','orgadmin',1),
    ('network reviewer role','ntwreviewer',1),
    ('system reviewer role','sysreviewer',1),
    ('organisation reviewer role','orgreviewer',1);
UNLOCK TABLES;



--
-- Dumping data for table `permission_role`
--

LOCK TABLES `permission_role` WRITE,`permission` AS p WRITE,`role` AS r WRITE;
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='sysreviwer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:remove' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:update' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:update' AND r.roleName='sysadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='sysreviwer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:remove' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:update' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:update' AND r.roleName='sysadmin';


INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:display' AND r.roleName='sysreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:update' AND r.roleName='sysadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:display' AND r.roleName='sysreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:update' AND r.roleName='sysadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:display' AND r.roleName='orgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:update' AND r.roleName='orgadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:display' AND r.roleName='orgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:update' AND r.roleName='orgadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:display' AND r.roleName='orgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:update' AND r.roleName='orgadmin';

INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:display' AND r.roleName='orgreviewer';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:update' AND r.roleName='Jedi';
INSERT IGNORE INTO `permission_role` (permission_id, roles_id)
SELECT p.id, r.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:update' AND r.roleName='orgadmin';

UNLOCK TABLES;



--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE,`permission` AS p WRITE,`role` AS r WRITE;

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:update' AND r.roleName='Jedi';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:display' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:create' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:remove' AND r.roleName='Jedi';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:update' AND r.roleName='Jedi';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:create' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:remove' AND r.roleName='ntwadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:update' AND r.roleName='ntwadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:remove' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:update' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwIPAddress:display' AND r.roleName='sysreviewer';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:remove' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:update' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwNICard:display' AND r.roleName='sysreviewer';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwDC:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwRarea:display' AND r.roleName='ntwreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiNtwSubnet:display' AND r.roleName='ntwreviewer';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:remove' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:update' AND r.roleName='sysadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:display' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:create' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:remove' AND r.roleName='sysadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:update' AND r.roleName='sysadmin';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOsi:display' AND r.roleName='sysreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComITiSysOst:display' AND r.roleName='sysreviewer';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:remove' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:update' AND r.roleName='orgadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:remove' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:update' AND r.roleName='orgadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:remove' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:update' AND r.roleName='orgadmin';

INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:display' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:create' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:remove' AND r.roleName='orgadmin';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:update' AND r.roleName='orgadmin';


INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgApp:display' AND r.roleName='orgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgCompany:display' AND r.roleName='orgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgEnvironment:display' AND r.roleName='orgreviewer';
INSERT IGNORE INTO `role_permission` (role_id, permissions_id)
SELECT r.id, p.id FROM permission AS p, role AS r WHERE p.permissionName='dirComOrgTeam:display' AND r.roleName='orgreviewer';
UNLOCK TABLES;