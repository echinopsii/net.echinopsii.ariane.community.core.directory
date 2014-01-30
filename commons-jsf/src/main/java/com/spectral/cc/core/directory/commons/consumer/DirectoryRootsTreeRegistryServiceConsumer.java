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

import com.spectral.cc.core.directory.commons.registry.DirectoryMenuRootsTreeRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iPojo singleton which consume the directory roots tree registry service
 * Instantiated during directory commons-jsf bundle startup. FactoryMethod : getInstance
 */
@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class DirectoryRootsTreeRegistryServiceConsumer {
    private static final Logger log = LoggerFactory.getLogger(DirectoryRootsTreeRegistryServiceConsumer.class);
    private static DirectoryRootsTreeRegistryServiceConsumer INSTANCE;

    @Requires
    private DirectoryMenuRootsTreeRegistry directoryMenuRootsTreeRegistry = null;

    @Bind
    public void bindDirectoryRootsTreeRegistry(DirectoryMenuRootsTreeRegistry r) {
        log.info("Consumer bound to directory roots tree registry...");
        directoryMenuRootsTreeRegistry = r;
    }

    @Unbind
    public void unbindDirectoryRootsTreeRegistry() {
        log.info("Consumer unbound from directory roots tree registry...");
        directoryMenuRootsTreeRegistry = null;
    }

    /**
     * Get binded directory roots tree registry service consumer
     *
     * @return directory roots tree registry service consumer binded on this consumer. If null no registry has been binded ...
     */
    public DirectoryMenuRootsTreeRegistry getDirectoryMenuRootsTreeRegistry() {
        return directoryMenuRootsTreeRegistry;
    }

    /**
     * Factory method for this singleton
     *
     * @return instantiated directory roots tree registry service consumer
     */
    public static DirectoryRootsTreeRegistryServiceConsumer getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DirectoryRootsTreeRegistryServiceConsumer();
        return INSTANCE;
    }
}