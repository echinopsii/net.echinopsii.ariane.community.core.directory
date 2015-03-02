/**
 * Directory wat
 * Directory Component Bootstrap
 * Copyright (C) 2013 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.echinopsii.ariane.community.core.directory.wat;

import com.fasterxml.jackson.core.JsonFactory;
import net.echinopsii.ariane.community.core.portal.base.plugin.FaceletsResourceResolverService;
import net.echinopsii.ariane.community.core.portal.base.plugin.FacesMBeanRegistry;
import net.echinopsii.ariane.community.core.portal.base.model.MainMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.model.MenuEntityType;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import net.echinopsii.ariane.community.core.portal.base.plugin.MainMenuEntityRegistry;
import net.echinopsii.ariane.community.core.portal.base.plugin.RestResourceRegistry;
import net.echinopsii.ariane.community.core.portal.base.plugin.TreeMenuRootsRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;

@Component
@Provides(properties= {@StaticServiceProperty(name="targetArianeComponent", type="java.lang.String", value="Portal")})
@Instantiate
public class DirectoryBootstrap implements FaceletsResourceResolverService {
    private static final Logger log = LoggerFactory.getLogger(DirectoryBootstrap.class);
    private static final String DIRECTORY_COMPONENT = "Ariane Directory Component";

    protected static ArrayList<MainMenuEntity> directoryMainMenuEntityList = new ArrayList<MainMenuEntity>() ;
    protected static ArrayList<TreeMenuEntity> directoryTreeEntityList     = new ArrayList<TreeMenuEntity>() ;

    private static String MAIN_MENU_DIRECTORY_CONTEXT;

    private static final int    MAIN_MENU_DIR_RANK = 2;
    private static final String basePath = "/META-INF";
    private static final String FACES_CONFIG_FILE_PATH = basePath + "/faces-config.xml";
    private static final String REST_EP_FILE_PATH = basePath + "/rest.endpoints";

    @Requires(from="ArianePortalFacesMBeanRegistry")
    private FacesMBeanRegistry portalPluginFacesMBeanRegistry = null;

    @Requires(from="DirectoryMenuRootsTreeRegistryImpl")
    private TreeMenuRootsRegistry treeMenuRootsRegistry = null;

    @Requires
    private MainMenuEntityRegistry mainMenuEntityRegistry = null;

    @Requires
    private RestResourceRegistry restResourceRegistry = null;

    private static JsonFactory jFactory = new JsonFactory();

    @Bind
    public void bindRestResourceRegistry(RestResourceRegistry r) {
        log.debug("Bound to rest resource registry...");
        restResourceRegistry = r;
    }

    @Unbind
    public void unbindRestResourceRegistry() {
        log.debug("Bound to rest resource registry...");
        restResourceRegistry = null;
    }

    @Bind
    public void bindMainMenuEntityRegistry(MainMenuEntityRegistry r) {
        log.debug("Bound to main menu item registry...");
        mainMenuEntityRegistry = r;
    }

    @Unbind
    public void unbindMainMenuEntityRegistry() {
        log.debug("Unbound from main menu item registry...");
        mainMenuEntityRegistry = null;
    }

    @Bind
    public void bindPortalPluginFacesMBeanRegistry(FacesMBeanRegistry r) {
        log.debug("Bound to portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = r;
    }

    @Unbind
    public void unbindPortalPluginFacesMBeanRegistry() {
        log.debug("Unbound from portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = null;
    }

    @Bind
    public void bindTreeMenuRootsRegistry(TreeMenuRootsRegistry r) {
        log.debug("Bound to directory tree menu roots registry...");
        treeMenuRootsRegistry = r;
    }

    @Unbind
    public void unbindTreeMenuRootsRegistry() {
        log.debug("Unbound from directory tree menu roots registry...");
        treeMenuRootsRegistry = null;
    }

    @Validate
    public void validate() throws Exception {
        restResourceRegistry.registerPluginRestEndpoints(DirectoryBootstrap.class.getResource(REST_EP_FILE_PATH));
        portalPluginFacesMBeanRegistry.registerPluginFacesMBeanConfig(DirectoryBootstrap.class.getResource(FACES_CONFIG_FILE_PATH));
        MAIN_MENU_DIRECTORY_CONTEXT = portalPluginFacesMBeanRegistry.getRegisteredServletContext().getContextPath()+"/";

        try {
            MainMenuEntity mainMenuEntity = new MainMenuEntity("directoriesMItem", "Directories", MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/main.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_DIR_RANK, "icon-directories-ariane");
            mainMenuEntity.getDisplayRoles().add("ntwadmin");
            mainMenuEntity.getDisplayRoles().add("sysadmin");
            mainMenuEntity.getDisplayRoles().add("orgadmin");
            mainMenuEntity.getDisplayRoles().add("ntwreviewer");
            mainMenuEntity.getDisplayRoles().add("sysreviewer");
            mainMenuEntity.getDisplayRoles().add("orgreviewer");
            mainMenuEntity.getDisplayPermissions().add("dirComITiNtwDC:display");
            mainMenuEntity.getDisplayPermissions().add("dirComITiNtwMarea:display");
            mainMenuEntity.getDisplayPermissions().add("dirComITiNtwSubnet:display");
            mainMenuEntity.getDisplayPermissions().add("dirComITiSysOsi:display");
            mainMenuEntity.getDisplayPermissions().add("dirComITiSysOst:display");
            mainMenuEntity.getDisplayPermissions().add("dirComOrgApp:display");
            mainMenuEntity.getDisplayPermissions().add("dirComOrgCompany:display");
            mainMenuEntity.getDisplayPermissions().add("dirComOrgEnvironment:display");
            mainMenuEntity.getDisplayPermissions().add("dirComOrgTeam:display");

            directoryMainMenuEntityList.add(mainMenuEntity);
            mainMenuEntityRegistry.registerMainLeftMenuEntity(mainMenuEntity);
            treeMenuRootsRegistry.setLinkedMainMenuEntity(mainMenuEntity);
            log.debug("{} has registered its main menu items", new Object[]{DIRECTORY_COMPONENT});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            TreeMenuEntity commonRootTreeMenuEntity = new TreeMenuEntity().setId("commonsDir").setValue("Common").setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                           addDisplayRole("ntwadmin").addDisplayRole("sysadmin").addDisplayRole("orgadmin").
                                                                           addDisplayRole("ntwreviewer").addDisplayRole("sysreviewer").addDisplayRole("orgreviewer").
                                                                           addDisplayPermission("dirComITiNtwDC:display").addDisplayPermission("dirComITiNtwRarea:display").
                                                                           addDisplayPermission("dirComITiNtwSubnet:display").addDisplayPermission("dirComITiSysOsi:display").
                                                                           addDisplayPermission("dirComITiSysOst:display").addDisplayPermission("dirComOrgApp:display").
                                                                           addDisplayPermission("dirComOrgCompany:display").addDisplayPermission("dirComOrgEnvironment:display").
                                                                           addDisplayPermission("dirComOrgTeam:display");
            directoryTreeEntityList.add(commonRootTreeMenuEntity);
            treeMenuRootsRegistry.registerTreeMenuRootEntity(commonRootTreeMenuEntity);


            TreeMenuEntity organisationalTreeMenuEntity = new TreeMenuEntity().setId("commonsOrgDir").setValue("Organisation").
                                                                               setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                               setParentTreeMenuEntity(commonRootTreeMenuEntity).
                                                                               addDisplayRole("orgadmin").addDisplayRole("orgreviewer").
                                                                               addDisplayPermission("dirComOrgApp:display").addDisplayPermission("dirComOrgCompany:display").
                                                                               addDisplayPermission("dirComOrgEnvironment:display").addDisplayPermission("dirComOrgTeam:display");
            commonRootTreeMenuEntity.addChildTreeMenuEntity(organisationalTreeMenuEntity);


            organisationalTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("applicationTreeID").setValue("Application").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("arianeico-appplications-ariane").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/application.jsf").
                                                                                     setDescription("Your IT applications definitions").
                                                                                     addDisplayRole("orgadmin").addDisplayRole("orgreviewer").addDisplayPermission("dirComOrgApp:display")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("teamTreeID").setValue("Team").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-group").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/team.jsf").
                                                                                     setDescription("Your teams (ops, devs, ...) definitions").
                                                                                     addDisplayRole("orgreviewer").addDisplayRole("orgadmin").addDisplayPermission("dirComOrgTeam:display")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("companyTreeID").setValue("Company").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-building").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/company.jsf").
                                                                                     setDescription("Definition of companies involved in your IT system (yours included)").
                                                                                     addDisplayRole("orgreviewer").addDisplayRole("orgadmin").addDisplayPermission("dirComOrgCompany:display")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("environmentTreeID").setValue("Environment").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-tag").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/environment.jsf").
                                                                                     setDescription("Your IT environment (development, homologation, QA, production ...)").
                                                                                     addDisplayRole("orgreviewer").addDisplayRole("orgadmin").addDisplayPermission("dirComOrgEnvironment:display"));



            TreeMenuEntity technicalTreeMenuEntity = new TreeMenuEntity().setId("commonsTechDir").setValue("IT infrastructure").
                                                                          setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                          setParentTreeMenuEntity(commonRootTreeMenuEntity).
                                                                          addDisplayRole("ntwadmin").addDisplayRole("sysadmin").
                                                                          addDisplayRole("ntwreviewer").addDisplayRole("sysreviewer").addDisplayPermission("dirComITiNtwSubnet:display").
                                                                          addDisplayPermission("dirComITiNtwDC:display").addDisplayPermission("dirComITiNtwRarea:display");
            commonRootTreeMenuEntity.addChildTreeMenuEntity(technicalTreeMenuEntity);


            TreeMenuEntity networkTreeMenuEntity = new TreeMenuEntity().setId("commontNtwDir").setValue("Network").
                                                                        setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                        setParentTreeMenuEntity(technicalTreeMenuEntity).
                                                                        addDisplayRole("ntwreviewer").addDisplayRole("ntwadmin").
                                                                        addDisplayPermission("dirComITiNtwDC:display").addDisplayPermission("dirComITiNtwRarea:display");
            technicalTreeMenuEntity.addChildTreeMenuEntity(networkTreeMenuEntity);
            networkTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("datacenterTreeID").setValue("Datacenter").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("arianeico-datacenter-ariane").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/datacenter.jsf").
                                                                              setDescription("Your datacenters definitions").addDisplayRole("ntwreviewer").addDisplayRole("ntwadmin").
                                                                              addDisplayPermission("dirComITiNtwDC:display")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("subnetTreeID").setValue("Subnet").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("arianeico-subnet-ariane").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/subnet.jsf").
                                                                              setDescription("Your subnets definitions").addDisplayRole("ntwreviewer").addDisplayRole("ntwadmin").
                                                                              addDisplayPermission("dirComITiNtwSubnet:display")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("multicastAreaTreeID").setValue("Routing Area").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("arianeico-routingarea-ariane").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/routingArea.jsf").
                                                                              setDescription("Your routing areas definitions").addDisplayRole("ntwreviewer").addDisplayRole("ntwadmin").
                                                                              addDisplayPermission("dirComITiNtwRarea:display"));


            TreeMenuEntity systemTreeMenuEntity = new TreeMenuEntity().setId("commonSysDir").setValue("System").
                                                                       setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                       setParentTreeMenuEntity(technicalTreeMenuEntity).
                                                                       addDisplayRole("sysreviewer").addDisplayRole("sysadmin").addDisplayPermission("dirComITiSysOsi:display").
                                                                       addDisplayPermission("dirComITiSysOst:display");
            technicalTreeMenuEntity.addChildTreeMenuEntity(systemTreeMenuEntity);
            systemTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("OSInstanceTreeID").setValue("OS Instance").setParentTreeMenuEntity(systemTreeMenuEntity).setIcon("arianeico-os-instances-ariane").
                                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/OSInstance.jsf").
                                                                             setDescription("Your OS instances definitions").addDisplayRole("sysreviewer").addDisplayRole("sysadmin").
                                                                             addDisplayPermission("dirComITiSysOsi:display")).
                                 addChildTreeMenuEntity(new TreeMenuEntity().setId("OSTypeTreeID").setValue("OS Type").setParentTreeMenuEntity(systemTreeMenuEntity).setIcon("icon-tag").
                                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/OSType.jsf").
                                                                             setDescription("Your OS types definitions").addDisplayRole("sysreviewer").addDisplayRole("sysadmin").
                                                                             addDisplayPermission("dirComITiSysOst:display"));

            log.debug("{} has registered its commons directory items", new Object[]{DIRECTORY_COMPONENT});

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        log.info("{} is started", new Object[]{DIRECTORY_COMPONENT});
    }

    @Invalidate
    public void invalidate() throws Exception {
        if (mainMenuEntityRegistry!=null) {
            for(MainMenuEntity entity : directoryMainMenuEntityList) {
                mainMenuEntityRegistry.unregisterMainLeftMenuEntity(entity);
            }
        }
        directoryMainMenuEntityList.clear();

        if (treeMenuRootsRegistry!=null) {
            for(TreeMenuEntity entity : directoryTreeEntityList) {
                treeMenuRootsRegistry.unregisterTreeMenuRootEntity(entity);
            }
        }
        directoryTreeEntityList.clear();
        restResourceRegistry.unregisterPluginRestEndpoints(DirectoryBootstrap.class.getResource(REST_EP_FILE_PATH));

        log.info("{} is stopped", new Object[]{DIRECTORY_COMPONENT});
    }

    public static JsonFactory getjFactory() {
        return jFactory;
    }

    @Override
    public URL resolveURL(String path) {
        log.debug("Resolve {} from directory wat...", new Object[]{path});
        return DirectoryBootstrap.class.getResource(basePath + path);
    }
}