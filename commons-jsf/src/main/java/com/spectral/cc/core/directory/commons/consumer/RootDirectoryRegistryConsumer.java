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

import com.spectral.cc.core.directory.commons.registry.RootDirectoryRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class RootDirectoryRegistryConsumer {
    private static final Logger log = LoggerFactory.getLogger(RootDirectoryRegistryConsumer.class);
    private static RootDirectoryRegistryConsumer INSTANCE;

    @Requires
    private RootDirectoryRegistry rootDirectoryRegistry = null;

    @Bind
    public void bindRootDirectoryRegistry(RootDirectoryRegistry r) {
        log.debug("Consumer bound to root directory registry...");
        rootDirectoryRegistry = r;
    }

    @Unbind
    public void unbindRootDirectoryRegistry() {
        log.debug("Consumer unbound from root directory registry...");
        rootDirectoryRegistry = null;
    }

    public RootDirectoryRegistry getRootDirectoryRegistry() {
        return rootDirectoryRegistry;
    }

    public static RootDirectoryRegistryConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RootDirectoryRegistryConsumer();
        }
        return INSTANCE;
    }
}