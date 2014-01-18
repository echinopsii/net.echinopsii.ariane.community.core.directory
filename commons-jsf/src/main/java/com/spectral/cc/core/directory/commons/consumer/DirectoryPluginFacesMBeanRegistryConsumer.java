/**
 * [DEFINE YOUR PROJECT NAME/MODULE HERE]
 * [DEFINE YOUR PROJECT DESCRIPTION HERE] 
 * Copyright (C) 12/01/14 echinopsii
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

import com.spectral.cc.core.portal.commons.facesplugin.PluginFacesMBeanRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class DirectoryPluginFacesMBeanRegistryConsumer {
    private static final Logger log = LoggerFactory.getLogger(DirectoryPluginFacesMBeanRegistryConsumer.class);
    private static DirectoryPluginFacesMBeanRegistryConsumer INSTANCE;

    @Requires(from="DirectoryPluginFacesMBeanRegistryImpl")
    private PluginFacesMBeanRegistry directoryPluginFacesMBeanDirectoryRegistry = null;

    @Bind
    public void bindPluginFacesMBeanDirectoryRegistry(PluginFacesMBeanRegistry r) {
        log.info("Consumer bound to directory plugin faces managed bean registry...");
        directoryPluginFacesMBeanDirectoryRegistry = r;
    }

    @Unbind
    public void unbindPluginFacesMBeanDirectoryRegistry() {
        log.info("Consumer unbound from directory plugin faces managed bean registry...");
        directoryPluginFacesMBeanDirectoryRegistry = null;
    }

    public PluginFacesMBeanRegistry getDirectoryPluginFacesMBeanRegistry() {
        return directoryPluginFacesMBeanDirectoryRegistry;
    }

    public static DirectoryPluginFacesMBeanRegistryConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DirectoryPluginFacesMBeanRegistryConsumer();
        }
        return INSTANCE;
    }
}