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

import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.spectral.cc.core.directory.commons.persistence.JPAProvider;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class JPAProviderConsumer {
    private static final Logger log = LoggerFactory.getLogger(JPAProviderConsumer.class);
    private static JPAProviderConsumer INSTANCE;

    @Requires
    private JPAProvider jpaProvider = null;

    @Bind
    public void bindRootDirectoryRegistry(JPAProvider r) {
        log.debug("Consumer bound to directory JPA provider...");
        jpaProvider = r;
    }

    @Unbind
    public void unbindRootDirectoryRegistry() {
        log.debug("Consumer unbound from directory JPA provider...");
        jpaProvider = null;
    }

    public JPAProvider getJpaProvider() {
        return jpaProvider;
    }

    public static JPAProviderConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new JPAProviderConsumer();
        }
        return INSTANCE;
    }
}