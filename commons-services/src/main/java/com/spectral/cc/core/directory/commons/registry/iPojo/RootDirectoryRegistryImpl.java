/**
 * Directory Commons Services bundle
 * Directory Registry iPojo Impl
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
package com.spectral.cc.core.directory.commons.registry.iPojo;

import com.spectral.cc.core.directory.commons.model.DirectoryEntity;
import com.spectral.cc.core.directory.commons.registry.RootDirectoryRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

@Component
@Provides
@Instantiate
public class RootDirectoryRegistryImpl implements RootDirectoryRegistry {

    private static final String ROOT_DIRECTORY_REGISTRY_SERVICE_NAME = "Root Directory Registry Service";
    private static final Logger log = LoggerFactory.getLogger(RootDirectoryRegistryImpl.class);

    private TreeSet<DirectoryEntity> registry = new TreeSet<DirectoryEntity>();

    @Validate
    public void validate() throws Exception {
        log.debug("{} is started.", new Object[]{ROOT_DIRECTORY_REGISTRY_SERVICE_NAME});
    }

    @Invalidate
    public void invalidate(){
        log.debug("Stopping {}...", new Object[]{ROOT_DIRECTORY_REGISTRY_SERVICE_NAME});
        registry.clear();
        log.debug("{} is stopped.", new Object[]{ROOT_DIRECTORY_REGISTRY_SERVICE_NAME});
    }

    @Override
    public DirectoryEntity registerRootDirectoryEntity(DirectoryEntity directoryEntity) throws Exception {
        this.registry.add(directoryEntity);
        return directoryEntity;
    }

    @Override
    public DirectoryEntity unregisterRootDirectoryEntity(DirectoryEntity directoryEntity) throws Exception {
        this.registry.remove(directoryEntity);
        return directoryEntity;
    }

    @Override
    public TreeSet<DirectoryEntity> getRootDirectoryEntities() {
        return registry;
    }

    @Override
    public DirectoryEntity getDirectoryEntityFromValue(String value) {
        DirectoryEntity ret = null;
        for (DirectoryEntity entity : registry) {
            ret = entity.findDirectoryEntityFromValue(value);
            if (ret!=null)
                break;
        }
        return ret;
    }

    @Override
    public DirectoryEntity getDirectoryEntityFromID(String id) {
        DirectoryEntity ret = null;
        for (DirectoryEntity entity : registry) {
            ret = entity.findDirectoryEntityFromID(id);
            if (ret!=null)
                break;
        }
        return ret;
    }
}