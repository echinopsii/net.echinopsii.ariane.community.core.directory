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
            MainMenuEntity mainMenuEntity = new MainMenuEntity("directoriesMItem", "Directories", MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/main.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_DIR_RANK, "icon-book icon-large");
            mainMenuEntity.getDisplayRoles().add("ccntwadmin");
            mainMenuEntity.getDisplayRoles().add("ccsysadmin");
            mainMenuEntity.getDisplayRoles().add("ccorgadmin");
            mainMenuEntity.getDisplayRoles().add("ccntwreviewer");
            mainMenuEntity.getDisplayRoles().add("ccsysreviewer");
            mainMenuEntity.getDisplayRoles().add("ccorgreviewer");
            mainMenuEntity.getDisplayPermissions().add("ccDirComITiNtwDC:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComITiNtwMarea:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComITiNtwSubnet:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComITiSysOsi:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComITiSysOst:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComOrgApp:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComOrgCompany:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComOrgEnvironment:display");
            mainMenuEntity.getDisplayPermissions().add("ccDirComOrgTeam:display");

            directoryMainMenuEntityList.add(mainMenuEntity);
            mainMenuEntityRegistry.registerMainMenuEntity(mainMenuEntity);
            treeMenuRootsRegistry.setLinkedMainMenuEntity(mainMenuEntity);
            log.debug("{} has registered its main menu items", new Object[]{DIRECTORY_COMPONENT});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            TreeMenuEntity commonRootTreeMenuEntity = new TreeMenuEntity().setId("commonsDir").setValue("Common").setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                           addDisplayRole("ccntwadmin").addDisplayRole("ccsysadmin").addDisplayRole("ccorgadmin").
                                                                           addDisplayRole("ccntwreviewer").addDisplayRole("ccsysreviewer").addDisplayRole("ccorgreviewer").
                                                                           addDisplayPermission("ccDirComITiNtwDC:display").addDisplayPermission("ccDirComITiNtwMarea:display").
                                                                           addDisplayPermission("ccDirComITiNtwSubnet:display").addDisplayPermission("ccDirComITiSysOsi:display").
                                                                           addDisplayPermission("ccDirComITiSysOst:display").addDisplayPermission("ccDirComOrgApp:display").
                                                                           addDisplayPermission("ccDirComOrgCompany:display").addDisplayPermission("ccDirComOrgEnvironment:display").
                                                                           addDisplayPermission("ccDirComOrgTeam:display");
            directoryTreeEntityList.add(commonRootTreeMenuEntity);
            treeMenuRootsRegistry.registerTreeMenuRootEntity(commonRootTreeMenuEntity);


            TreeMenuEntity organisationalTreeMenuEntity = new TreeMenuEntity().setId("commonsOrgDir").setValue("Organisation").
                                                                               setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                               setParentTreeMenuEntity(commonRootTreeMenuEntity).
                                                                               addDisplayRole("ccorgadmin").addDisplayRole("ccorgreviewer").
                                                                               addDisplayPermission("ccDirComOrgApp:display").addDisplayPermission("ccDirComOrgCompany:display").
                                                                               addDisplayPermission("ccDirComOrgEnvironment:display").addDisplayPermission("ccDirComOrgTeam:display");
            commonRootTreeMenuEntity.addChildTreeMenuEntity(organisationalTreeMenuEntity);


            organisationalTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("applicationTreeID").setValue("Application").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-cogs").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/application.jsf").
                                                                                     setDescription("Your IT applications definitions").
                                                                                     addDisplayRole("ccorgadmin").addDisplayRole("ccorgreviewer").addDisplayPermission("ccDirComOrgApp:display")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("teamTreeID").setValue("Team").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-group").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/team.jsf").
                                                                                     setDescription("Your teams (ops, devs, ...) definitions").
                                                                                     addDisplayRole("ccorgreviewer").addDisplayRole("ccorgadmin").addDisplayPermission("ccDirComOrgTeam:display")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("companyTreeID").setValue("Company").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-building").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/company.jsf").
                                                                                     setDescription("Definition of companies involved in your IT system (yours included)").
                                                                                     addDisplayRole("ccorgreviewer").addDisplayRole("ccorgadmin").addDisplayPermission("ccDirComOrgCompany:display")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("environmentTreeID").setValue("Environment").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-tag").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/environment.jsf").
                                                                                     setDescription("Your IT environment (development, homologation, QA, production ...)").
                                                                                     addDisplayRole("ccorgreviewer").addDisplayRole("ccorgadmin").addDisplayPermission("ccDirComOrgEnvironment:display"));



            TreeMenuEntity technicalTreeMenuEntity = new TreeMenuEntity().setId("commonsTechDir").setValue("IT infrastructure").
                                                                          setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                          setParentTreeMenuEntity(commonRootTreeMenuEntity).
                                                                          addDisplayRole("ccntwadmin").addDisplayRole("ccsysadmin").
                                                                          addDisplayRole("ccntwreviewer").addDisplayRole("ccsysreviewer").addDisplayPermission("ccDirComITiNtwSubnet:display").
                                                                          addDisplayPermission("ccDirComITiNtwDC:display").addDisplayPermission("ccDirComITiNtwMarea:display");
            commonRootTreeMenuEntity.addChildTreeMenuEntity(technicalTreeMenuEntity);


            TreeMenuEntity networkTreeMenuEntity = new TreeMenuEntity().setId("commontNtwDir").setValue("Network").
                                                                        setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                        setParentTreeMenuEntity(technicalTreeMenuEntity).
                                                                        addDisplayRole("ccntwreviewer").addDisplayRole("ccntwadmin").
                                                                        addDisplayPermission("ccDirComITiNtwDC:display").addDisplayPermission("ccDirComITiNtwMarea:display");
            technicalTreeMenuEntity.addChildTreeMenuEntity(networkTreeMenuEntity);
            networkTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("datacenterTreeID").setValue("Datacenter").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("icon-building").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/datacenter.jsf").
                                                                              setDescription("Your datacenters definitions").addDisplayRole("ccntwreviewer").addDisplayRole("ccntwadmin").
                                                                              addDisplayPermission("ccDirComITiNtwDC:display")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("subnetTreeID").setValue("Subnet").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("icon-road").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/subnet.jsf").
                                                                              setDescription("Your subnets definitions").addDisplayRole("ccntwreviewer").addDisplayRole("ccntwadmin").
                                                                              addDisplayPermission("ccDirComITiNtwSubnet:display")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("multicastAreaTreeID").setValue("Multicast area").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("icon-asterisk").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/multicastArea.jsf").
                                                                              setDescription("Your multicast areas definitions").addDisplayRole("ccntwreviewer").addDisplayRole("ccntwadmin").
                                                                              addDisplayPermission("ccDirComITiNtwMarea:display"));


            TreeMenuEntity systemTreeMenuEntity = new TreeMenuEntity().setId("commonSysDir").setValue("System").
                                                                       setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                       setParentTreeMenuEntity(technicalTreeMenuEntity).
                                                                       addDisplayRole("ccsysreviewer").addDisplayRole("ccsysadmin").addDisplayPermission("ccDirComITiSysOsi:display").
                                                                       addDisplayPermission("ccDirComITiSysOst:display");
            technicalTreeMenuEntity.addChildTreeMenuEntity(systemTreeMenuEntity);
            systemTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("OSInstanceTreeID").setValue("OS Instance").setParentTreeMenuEntity(systemTreeMenuEntity).setIcon("icon-cogs").
                                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/OSInstance.jsf").
                                                                             setDescription("Your OS instances definitions").addDisplayRole("ccsysreviewer").addDisplayRole("ccsysadmin").
                                                                             addDisplayPermission("ccDirComITiSysOsi:display")).
                                 addChildTreeMenuEntity(new TreeMenuEntity().setId("OSTypeTreeID").setValue("OS Type").setParentTreeMenuEntity(systemTreeMenuEntity).setIcon("icon-tag").
                                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/directories/OSType.jsf").
                                                                             setDescription("Your OS types definitions").addDisplayRole("ccsysreviewer").addDisplayRole("ccsysadmin").
                                                                             addDisplayPermission("ccDirComITiSysOst:display"));

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
                mainMenuEntityRegistry.unregisterMainMenuEntity(entity);
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