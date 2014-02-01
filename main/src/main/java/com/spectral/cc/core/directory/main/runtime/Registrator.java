/**
 * Directory Main
 * Registrator
 * Copyright (C) 2013 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package com.spectral.cc.core.directory.main.runtime;

import com.spectral.cc.core.directory.commons.consumer.DirectoryPluginFacesMBeanRegistryConsumer;
import com.spectral.cc.core.directory.commons.consumer.DirectoryTreeMenuRootsRegistryServiceConsumer;
import com.spectral.cc.core.portal.commons.model.TreeMenuEntity;
import com.spectral.cc.core.portal.commons.consumer.MainMenuRegistryConsumer;
import com.spectral.cc.core.portal.commons.model.MainMenuEntity;
import com.spectral.cc.core.portal.commons.model.MenuEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Registrator implements Runnable {

    private static final String DIRECTORY_REGISTRATOR_TASK_NAME = "Directory Registrator Task";
    private static final Logger log = LoggerFactory.getLogger(Registrator.class);

    private static String MAIN_MENU_DIRECTORY_CONTEXT;
    private static int MAIN_MENU_DIR_RANK = 2;

    @Override
    public void run() {
        //TODO : check a better way to start war after OSGI layer
        while((DirectoryPluginFacesMBeanRegistryConsumer.getInstance().getDirectoryPluginFacesMBeanRegistry()==null) ||
                      (DirectoryPluginFacesMBeanRegistryConsumer.getInstance().getDirectoryPluginFacesMBeanRegistry().getRegisteredServletContext()==null))
            try {
                log.info("Directory plugin faces managed bean registry is missing or is still not initialized to load {}. Sleep some times...", DIRECTORY_REGISTRATOR_TASK_NAME);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        MAIN_MENU_DIRECTORY_CONTEXT = DirectoryPluginFacesMBeanRegistryConsumer.getInstance().getDirectoryPluginFacesMBeanRegistry().getRegisteredServletContext().getContextPath()+"/";

        //TODO : check a better way to start war after OSGI layer
        while(MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry()==null)
            try {
                log.info("Portal main menu registry is missing to load {}. Sleep some times...", DIRECTORY_REGISTRATOR_TASK_NAME);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        try {
            MainMenuEntity mainMenuEntity = new MainMenuEntity("directoriesMItem", "Directories", MAIN_MENU_DIRECTORY_CONTEXT + "views/main.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_DIR_RANK, "icon-book icon-large");
            OsgiActivator.directoryMainMenuEntityList.add(mainMenuEntity);
            MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry().registerMainMenuEntity(mainMenuEntity);
            log.debug("{} has registered its main menu items", new Object[]{DIRECTORY_REGISTRATOR_TASK_NAME});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //TODO : check a better way to start war after OSGI layer
        while(DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry()==null)
            try {
                log.info("Directory tree menu roots registry is missing to load {}. Sleep some times...", DIRECTORY_REGISTRATOR_TASK_NAME);
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        try {
            TreeMenuEntity commonRootTreeMenuEntity = new TreeMenuEntity().setId("commonsDir").setValue("Common").setType(MenuEntityType.TYPE_MENU_SUBMENU);
            OsgiActivator.directoryTreeEntityList.add(commonRootTreeMenuEntity);
            DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().registerTreeMenuRootEntity(commonRootTreeMenuEntity);


            TreeMenuEntity organisationalTreeMenuEntity = new TreeMenuEntity().setId("commonsOrgDir").setValue("Organisation").
                                                                                  setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                                  setParentTreeMenuEntity(commonRootTreeMenuEntity);
            commonRootTreeMenuEntity.addChildTreeMenuEntity(organisationalTreeMenuEntity);
            organisationalTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("applicationTreeID").setValue("Application").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-cogs").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/application.jsf").
                                                                                     setDescription("Your IT applications definitions")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("teamTreeID").setValue("Team").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-group").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/team.jsf").
                                                                                     setDescription("Your teams (ops, devs, ...) definitions")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("companyTreeID").setValue("Company").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-building").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/company.jsf").
                                                                                     setDescription("Definition of companies involved in your IT system (yours included)")).
                                         addChildTreeMenuEntity(new TreeMenuEntity().setId("environmentTreeID").setValue("Environment").setParentTreeMenuEntity(organisationalTreeMenuEntity).setIcon("icon-tag").
                                                                                     setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/environment.jsf").
                                                                                     setDescription("Your IT environment (development, homologation, QA, production ...)"));

            TreeMenuEntity technicalTreeMenuEntity = new TreeMenuEntity().setId("commonsTechDir").setValue("IT infrastructure").
                                                                          setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                          setParentTreeMenuEntity(commonRootTreeMenuEntity);
            commonRootTreeMenuEntity.addChildTreeMenuEntity(technicalTreeMenuEntity);

            TreeMenuEntity networkTreeMenuEntity = new TreeMenuEntity().setId("commontNtwDir").setValue("Network").
                                                                        setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                        setParentTreeMenuEntity(technicalTreeMenuEntity);
            technicalTreeMenuEntity.addChildTreeMenuEntity(networkTreeMenuEntity);
            networkTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("datacenterTreeID").setValue("Datacenter").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("icon-building").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/datacenter.jsf").
                                                                              setDescription("Your datacenters definitions")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("subnetTreeID").setValue("Subnet").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("icon-road").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/subnet.jsf").
                                                                              setDescription("Your subnets definitions")).
                                  addChildTreeMenuEntity(new TreeMenuEntity().setId("multicastAreaTreeID").setValue("Multicast area").setParentTreeMenuEntity(networkTreeMenuEntity).setIcon("icon-asterisk").
                                                                              setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/multicastArea.jsf").
                                                                              setDescription("Your multicast areas definitions"));

            TreeMenuEntity systemTreeMenuEntity = new TreeMenuEntity().setId("commonSysDir").setValue("System").
                                                                       setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                       setParentTreeMenuEntity(technicalTreeMenuEntity);
            technicalTreeMenuEntity.addChildTreeMenuEntity(systemTreeMenuEntity);
            systemTreeMenuEntity.addChildTreeMenuEntity(new TreeMenuEntity().setId("OSInstanceTreeID").setValue("OS Instance").setParentTreeMenuEntity(systemTreeMenuEntity).setIcon("icon-cogs").
                                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/OSInstance.jsf").
                                                                             setDescription("Your OS instances definitions")).
                                 addChildTreeMenuEntity(new TreeMenuEntity().setId("OSTypeTreeID").setValue("OS Type").setParentTreeMenuEntity(systemTreeMenuEntity).setIcon("icon-tag").
                                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/OSType.jsf").
                                                                             setDescription("Your OS types definitions"));

            log.debug("{} has registered its commons directory items", new Object[]{DIRECTORY_REGISTRATOR_TASK_NAME});

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}