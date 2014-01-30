/**
 * Directory Commons Services bundle
 * Root Directory Registry Interface
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
package com.spectral.cc.core.directory.commons.registry;

import com.spectral.cc.core.directory.commons.model.DirectoryMenuEntity;

import java.util.TreeSet;

/**
 * The directory menu roots tree registry store the root directory menu entity.
 */
public interface DirectoryMenuRootsTreeRegistry {
    /**
     * Add a new root directory menu entity to registry
     *
     * @param directoryMenuEntity
     *
     * @return registered directory menu entity
     */
    public DirectoryMenuEntity registerRootDirectoryMenuEntity(DirectoryMenuEntity directoryMenuEntity);

    /**
     * Remove the directory menu entity from registry
     *
     * @param directoryMenuEntity
     *
     * @return unregistered directory menu entity
     */
    public DirectoryMenuEntity unregisterRootDirectoryMenuEntity(DirectoryMenuEntity directoryMenuEntity);

    /**
     * Get the registry
     *
     * @return the registry
     */
    public TreeSet<DirectoryMenuEntity> getRootDirectoryMenuEntities();

    /**
     * Get the root directory menu entity from value
     *
     * @param value
     *
     * @return if found the root directory menu entity else null
     */
    public DirectoryMenuEntity getDirectoryMenuEntityFromValue(String value);

    /**
     * Get the root directory menu entity from id
     *
     * @param id
     *
     * @return if found the root directory menu entity else null
     */
    public DirectoryMenuEntity getDirectoryMenuEntityFromID(String id);
}