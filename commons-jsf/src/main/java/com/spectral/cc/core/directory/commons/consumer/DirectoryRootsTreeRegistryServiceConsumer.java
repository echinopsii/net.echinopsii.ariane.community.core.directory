/**
 * Directory Commons JSF bundle
 * Root Directory Registry consumer singleton
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

package com.spectral.cc.core.directory.commons.consumer;

import com.spectral.cc.core.directory.commons.registry.DirectoryRootsTreeRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class DirectoryRootsTreeRegistryServiceConsumer {
    private static final Logger log = LoggerFactory.getLogger(DirectoryRootsTreeRegistryServiceConsumer.class);
    private static DirectoryRootsTreeRegistryServiceConsumer INSTANCE;

    @Requires
    private DirectoryRootsTreeRegistry directoryRootsTreeRegistry = null;

    @Bind
    public void bindDirectoryRootsTreeRegistry(DirectoryRootsTreeRegistry r) {
        log.info("Consumer bound to directory roots tree registry...");
        directoryRootsTreeRegistry = r;
    }

    @Unbind
    public void unbindDirectoryRootsTreeRegistry() {
        log.info("Consumer unbound from directory roots tree registry...");
        directoryRootsTreeRegistry = null;
    }

    public DirectoryRootsTreeRegistry getDirectoryRootsTreeRegistry() {
        return directoryRootsTreeRegistry;
    }

    public static DirectoryRootsTreeRegistryServiceConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DirectoryRootsTreeRegistryServiceConsumer();
        }
        return INSTANCE;
    }
}