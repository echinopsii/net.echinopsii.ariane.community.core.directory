/**
 * Directory Main
 * PersistenceConsumer
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

import com.spectral.cc.core.directory.commons.persistence.DirectoryJPAProvider;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * iPojo singleton which consume the directory JPA provider.
 * Instantiated during directory commons-jsf bundle startup. FactoryMethod : getInstance
 */
@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class DirectoryJPAProviderConsumer {
    private static final Logger log = LoggerFactory.getLogger(DirectoryJPAProviderConsumer.class);
    private static DirectoryJPAProviderConsumer INSTANCE;

    @Requires
    private DirectoryJPAProvider directoryJpaProvider = null;

    @Bind
    public void bindJPAProvider(DirectoryJPAProvider r) {
        log.info("Consumer bound to directory JPA provider...");
        directoryJpaProvider = r;
    }

    @Unbind
    public void unbindJPAProvider() {
        log.info("Consumer unbound from directory JPA provider...");
        directoryJpaProvider = null;
    }

    /**
     * Get directory JPA provider binded to this consumer...
     *
     * @return the binded directory JPA provider. null if unbinded.
     */
    public DirectoryJPAProvider getDirectoryJpaProvider() {
        return directoryJpaProvider;
    }

    /**
     * Factory method for this singleton...
     *
     * @return instantiated directory JPA provider consumer
     */
    public static DirectoryJPAProviderConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DirectoryJPAProviderConsumer();
        }
        return INSTANCE;
    }
}