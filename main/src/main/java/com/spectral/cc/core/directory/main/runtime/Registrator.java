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

import com.spectral.cc.core.directory.commons.consumer.RootDirectoryRegistryServiceConsumer;
import com.spectral.cc.core.directory.commons.model.DirectoryEntity;
import com.spectral.cc.core.portal.commons.consumer.MainMenuRegistryConsumer;
import com.spectral.cc.core.portal.commons.model.MainMenuEntity;
import com.spectral.cc.core.portal.commons.model.MenuEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Registrator implements Runnable {

    private static final String DIRECTORY_REGISTRATOR_SERVICE_NAME  = "Directory Registrator Service";
    private static final Logger log = LoggerFactory.getLogger(Registrator.class);

    private static String MAIN_MENU_DIRECTORY_CONTEXT = "/CCdirectory/";
    private static int MAIN_MENU_DIR_RANK = 2;

    @Override
    public void run() {
        //TODO : check a better way to start war after OSGI layer
        while(MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry()==null)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        try {
            MainMenuEntity mainMenuEntity = new MainMenuEntity("directoriesMItem", "Directories", MAIN_MENU_DIRECTORY_CONTEXT + "views/main.jsf", MenuEntityType.TYPE_MENU_ITEM, MAIN_MENU_DIR_RANK, "icon-book icon-large");
            OsgiActivator.directoryMainMenuEntityList.add(mainMenuEntity);
            MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry().registerMainMenuEntity(mainMenuEntity);
            log.debug("{} has registered its main menu items", new Object[]{DIRECTORY_REGISTRATOR_SERVICE_NAME});
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        //TODO : check a better way to start war after OSGI layer
        while(RootDirectoryRegistryServiceConsumer.getInstance().getRootDirectoryRegistry()==null)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        try {
            DirectoryEntity commonRootDirectoryEntity = new DirectoryEntity().setId("commonsDir").setValue("Common referential").setType(MenuEntityType.TYPE_MENU_SUBMENU);
            OsgiActivator.directoryTreeEntityList.add(commonRootDirectoryEntity);
            RootDirectoryRegistryServiceConsumer.getInstance().getRootDirectoryRegistry().registerRootDirectoryEntity(commonRootDirectoryEntity);


            DirectoryEntity organisationalDirectoryEntity = new DirectoryEntity().setId("commonsOrgDir").setValue("Organisation").
                                                                                  setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                                  setParentDirectory(commonRootDirectoryEntity);
            commonRootDirectoryEntity.addChildDirectory(organisationalDirectoryEntity);
            organisationalDirectoryEntity.
                    addChildDirectory(new DirectoryEntity().setId("applicationDirID").setValue("Application").setParentDirectory(organisationalDirectoryEntity).setIcon("icon-cogs").
                                                            setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/application.jsf").
                                                            setDescription("Your IT applications definitions")).
                    addChildDirectory(new DirectoryEntity().setId("teamDirID").setValue("Team").setParentDirectory(organisationalDirectoryEntity).setIcon("icon-group").
                                                            setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/team.jsf").
                                                            setDescription("Your teams (ops, devs, ...) definitions")).
                    addChildDirectory(new DirectoryEntity().setId("companyDirID").setValue("Company").setParentDirectory(organisationalDirectoryEntity).setIcon("icon-building").
                                                            setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/company.jsf").
                                                            setDescription("Definition of companies involved in your IT system (yours included)")).
                    addChildDirectory(new DirectoryEntity().setId("environmentDirID").setValue("Environment").setParentDirectory(organisationalDirectoryEntity).setIcon("icon-tag").
                                                            setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/environment.jsf").
                                                            setDescription("Your IT environment (development, homologation, QA, production ...)"));//.
            /*
            addChildDirectory(new DirectoryEntity().setId("organisationUnitDirID").setValue("Organisation unit").setParentDirectory(organisationalDirectoryEntity).setIcon("icon-flag").
                                                            setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main.jsf").
                                                            setDescription("Your organisation units definitions"));
            */

            DirectoryEntity technicalDirectoryEntity = new DirectoryEntity().setId("commonsTechDir").setValue("IT infrastructure").
                                                                             setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                             setParentDirectory(commonRootDirectoryEntity);
            commonRootDirectoryEntity.addChildDirectory(technicalDirectoryEntity);

            DirectoryEntity networkDirectoryEntity = new DirectoryEntity().setId("commontNtwDir").setValue("Network").
                                                                           setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                           setParentDirectory(technicalDirectoryEntity);
            technicalDirectoryEntity.addChildDirectory(networkDirectoryEntity);
            networkDirectoryEntity.
                     addChildDirectory(new DirectoryEntity().setId("datacenterDirID").setValue("Datacenter").setParentDirectory(networkDirectoryEntity).setIcon("icon-building").
                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/datacenter.jsf").
                                                             setDescription("Your datacenters definitions")).
                     addChildDirectory(new DirectoryEntity().setId("lanDirID").setValue("Lan").setParentDirectory(networkDirectoryEntity).setIcon("icon-road").
                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/lan.jsf").
                                                             setDescription("Your LANs definitions")).
                     addChildDirectory(new DirectoryEntity().setId("multicastAreaDirID").setValue("Multicast area").setParentDirectory(networkDirectoryEntity).setIcon("icon-asterisk").
                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/multicastArea.jsf").
                                                             setDescription("Your multicast areas definitions"));

            DirectoryEntity systemDirectoryEntity = new DirectoryEntity().setId("commonSysDir").setValue("System").
                                                                         setType(MenuEntityType.TYPE_MENU_SUBMENU).
                                                                         setParentDirectory(technicalDirectoryEntity);
            technicalDirectoryEntity.addChildDirectory(systemDirectoryEntity);
            systemDirectoryEntity.
                     addChildDirectory(new DirectoryEntity().setId("OSInstanceDirID").setValue("OS Instance").setParentDirectory(systemDirectoryEntity).setIcon("icon-cogs").
                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/OSInstance.jsf").
                                                             setDescription("Your OS instances definitions")).
                     addChildDirectory(new DirectoryEntity().setId("OSTypeDirID").setValue("OS Type").setParentDirectory(systemDirectoryEntity).setIcon("icon-tag").
                                                             setType(MenuEntityType.TYPE_MENU_ITEM).setContextAddress(MAIN_MENU_DIRECTORY_CONTEXT + "views/main/OSType.jsf").
                                                             setDescription("Your OS types definitions"));

            log.debug("{} has registered its commons directory items", new Object[]{DIRECTORY_REGISTRATOR_SERVICE_NAME});

        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}