/**
 * Directory Main
 * OSGI activator
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

import com.spectral.cc.core.directory.commons.consumer.RootDirectoryRegistryConsumer;
import com.spectral.cc.core.directory.commons.model.DirectoryEntity;
import com.spectral.cc.core.portal.commons.consumer.MainMenuRegistryConsumer;
import com.spectral.cc.core.portal.commons.model.MainMenuEntity;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class OsgiActivator implements BundleActivator {

    private static final String DIRECTORY_SERVICE_NAME                     = "Directory Main Service";
    private static final Logger log = LoggerFactory.getLogger(OsgiActivator.class);

    protected static ArrayList<MainMenuEntity> directoryMainMenuEntityList = new ArrayList<MainMenuEntity>() ;
    protected static ArrayList<DirectoryEntity> directoryTreeEntityList    = new ArrayList<DirectoryEntity>();

    @Override
    public void start(BundleContext context) {
       new Thread(new Registrator()).start();
       new Thread(new TXPersistenceConsumer().setContext(context)).start();
       log.debug("{} is started.", new Object[]{DIRECTORY_SERVICE_NAME});
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry()!=null) {
            for(MainMenuEntity entity : directoryMainMenuEntityList) {
                MainMenuRegistryConsumer.getInstance().getMainMenuEntityRegistry().unregisterMainMenuEntity(entity);
            }
        }
        directoryMainMenuEntityList.clear();
        if (RootDirectoryRegistryConsumer.getInstance().getRootDirectoryRegistry()!=null) {
            for(DirectoryEntity entity : directoryTreeEntityList) {
                RootDirectoryRegistryConsumer.getInstance().getRootDirectoryRegistry().unregisterRootDirectoryEntity(entity);
            }
        }
        directoryTreeEntityList.clear();

        TXPersistenceConsumer.close();

        log.debug("{} is stopped.", new Object[]{DIRECTORY_SERVICE_NAME});
    }
}