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

import com.spectral.cc.core.portal.commons.model.TreeMenuEntity;
import com.spectral.cc.core.portal.commons.registry.TreeMenuRootsRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TreeSet;

/**
 * The directory tree menu roots registry store the directory tree menu root entities.
 *
 * This is the iPojo implementation of {@link TreeMenuRootsRegistry}. The component is instantiated at commons-services bundle startup.
 * It provides the {@link TreeMenuRootsRegistry} service.
 */
@Component
@Provides
@Instantiate(name="DirectoryMenuRootsTreeRegistryImpl")
public class DirectoryTreeMenuRootsRegistryImpl implements TreeMenuRootsRegistry {

    private static final String ROOT_DIRECTORY_REGISTRY_SERVICE_NAME = "Directory Tree Menu Roots Registry Service";
    private static final Logger log = LoggerFactory.getLogger(DirectoryTreeMenuRootsRegistryImpl.class);

    private TreeSet<TreeMenuEntity> registry = new TreeSet<TreeMenuEntity>();

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
    public TreeMenuEntity registerTreeMenuRootEntity(TreeMenuEntity treeMenuEntity) {
        this.registry.add(treeMenuEntity);
        return treeMenuEntity;
    }

    @Override
    public TreeMenuEntity unregisterTreeMenuRootEntity(TreeMenuEntity treeMenuEntity) {
        this.registry.remove(treeMenuEntity);
        return treeMenuEntity;
    }

    @Override
    public TreeSet<TreeMenuEntity> getTreeMenuRootsEntities() {
        return registry;
    }

    @Override
    public TreeMenuEntity getTreeMenuEntityFromValue(String value) {
        TreeMenuEntity ret = null;
        for (TreeMenuEntity entity : registry) {
            ret = entity.findTreeMenuEntityFromValue(value);
            if (ret!=null)
                break;
        }
        return ret;
    }

    @Override
    public TreeMenuEntity getTreeMenuEntityFromID(String id) {
        TreeMenuEntity ret = null;
        for (TreeMenuEntity entity : registry) {
            ret = entity.findTreeMenuEntityFromID(id);
            if (ret!=null)
                break;
        }
        return ret;
    }

    @Override
    public TreeMenuEntity getTreeMenuEntityFromContextAddress(String contextAddress) {
        TreeMenuEntity ret = null;
        for (TreeMenuEntity entity : registry) {
            ret = entity.findTreeMenuEntityFromContextAddress(contextAddress);
            if (ret!=null)
                break;
        }
        return ret;
    }
}