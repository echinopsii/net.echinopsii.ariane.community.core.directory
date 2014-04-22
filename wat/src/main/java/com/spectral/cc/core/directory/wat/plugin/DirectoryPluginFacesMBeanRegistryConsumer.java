/**
 * Directory wat
 * Portal Plugin Faces MBean registry consumer singleton
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

package com.spectral.cc.core.directory.wat.plugin;

import com.spectral.cc.core.portal.base.plugin.FacesMBeanRegistry;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * iPojo singleton which consume the plugin faces mbean registry implemented by PortalPluginFacesMBearRegistryImpl
 * Instantiated during directory commons-jsf bundle startup. FactoryMethod : getInstance
 */
@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class DirectoryPluginFacesMBeanRegistryConsumer {
    private static final Logger log = LoggerFactory.getLogger(DirectoryPluginFacesMBeanRegistryConsumer.class);
    private static DirectoryPluginFacesMBeanRegistryConsumer INSTANCE;

    @Requires(from="CCPortalFacesMBeanRegistry")
    private FacesMBeanRegistry portalPluginFacesMBeanRegistry = null;

    @Bind
    public void bindPortalPluginFacesMBeanRegistry(FacesMBeanRegistry r) {
        log.debug("Bound to portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = r;
    }

    @Unbind
    public void unbindPortalPluginFacesMBeanRegistry() {
        log.debug("Unbound from portal plugin faces managed bean registry...");
        portalPluginFacesMBeanRegistry = null;
    }

    /**
     * Get portal plugin faces managed bean registry
     *
     * @return the binded directory plugin faces managed bean registry. null if unbinded.
     */
    public FacesMBeanRegistry getPortalPluginFacesMBeanRegistry() {
        return portalPluginFacesMBeanRegistry;
    }

    /**
     * Factory method for this singleton...
     *
     * @return instantiated directory plugin faces mbean registry consumer
     */
    public static DirectoryPluginFacesMBeanRegistryConsumer getInstance() {
        if (INSTANCE == null)
            INSTANCE = new DirectoryPluginFacesMBeanRegistryConsumer();
        return INSTANCE;
    }
}