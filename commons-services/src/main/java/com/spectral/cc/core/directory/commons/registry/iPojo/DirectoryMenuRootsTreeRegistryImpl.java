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

import com.spectral.cc.core.directory.commons.model.DirectoryMenuEntity;
import com.spectral.cc.core.directory.commons.registry.DirectoryMenuRootsTreeRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

/**
 * The directory menu roots tree registry store the root directory menu entity.
 *
 * This is the iPojo implementation of {@link DirectoryMenuRootsTreeRegistry}. The component is instantiated at commons-services bundle startup.
 * It provides the {@link DirectoryMenuRootsTreeRegistry} service.
 */
@Component
@Provides
@Instantiate
public class DirectoryMenuRootsTreeRegistryImpl implements DirectoryMenuRootsTreeRegistry {

    private static final String ROOT_DIRECTORY_REGISTRY_SERVICE_NAME = "Directory Roots Tree Registry Service";
    private static final Logger log = LoggerFactory.getLogger(DirectoryMenuRootsTreeRegistryImpl.class);

    private TreeSet<DirectoryMenuEntity> registry = new TreeSet<DirectoryMenuEntity>();

    @Validate
    public void validate() throws Exception {
        log.info("{} is started.", new Object[]{ROOT_DIRECTORY_REGISTRY_SERVICE_NAME});
    }

    @Invalidate
    public void invalidate(){
        registry.clear();
        log.info("{} is stopped.", new Object[]{ROOT_DIRECTORY_REGISTRY_SERVICE_NAME});
    }

    @Override
    public DirectoryMenuEntity registerRootDirectoryMenuEntity(DirectoryMenuEntity directoryMenuEntity) {
        this.registry.add(directoryMenuEntity);
        return directoryMenuEntity;
    }

    @Override
    public DirectoryMenuEntity unregisterRootDirectoryMenuEntity(DirectoryMenuEntity directoryMenuEntity) {
        this.registry.remove(directoryMenuEntity);
        return directoryMenuEntity;
    }

    @Override
    public TreeSet<DirectoryMenuEntity> getRootDirectoryMenuEntities() {
        return registry;
    }

    @Override
    public DirectoryMenuEntity getDirectoryMenuEntityFromValue(String value) {
        DirectoryMenuEntity ret = null;
        for (DirectoryMenuEntity entity : registry) {
            ret = entity.findDirectoryMenuEntityFromValue(value);
            if (ret!=null)
                break;
        }
        return ret;
    }

    @Override
    public DirectoryMenuEntity getDirectoryMenuEntityFromID(String id) {
        DirectoryMenuEntity ret = null;
        for (DirectoryMenuEntity entity : registry) {
            ret = entity.findDirectoryMenuEntityFromID(id);
            if (ret!=null)
                break;
        }
        return ret;
    }
}