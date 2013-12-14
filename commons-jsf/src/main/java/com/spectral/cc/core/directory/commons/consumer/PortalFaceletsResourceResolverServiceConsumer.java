/**
 * Directory Commons JSF bundle
 * Portal Facelets Resource Resolver Service consumer singleton
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

import com.spectral.cc.core.portal.commons.service.FaceletsResourceResolverService;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class PortalFaceletsResourceResolverServiceConsumer {
    private static final Logger log = LoggerFactory.getLogger(PortalFaceletsResourceResolverServiceConsumer.class);
    private static PortalFaceletsResourceResolverServiceConsumer INSTANCE;

    @Requires
    private FaceletsResourceResolverService portalFaceletsResourceResolverService = null;

    @Bind
    public void bindPortalFaceletsResourceResolverService(FaceletsResourceResolverService r) {
        log.debug("Consumer bound to portal facelets resource resolver service...");
        portalFaceletsResourceResolverService = r;
    }

    @Unbind
    public void unbindPortalFaceletsResourceResolverService() {
        log.debug("Consumer unbound from portal facelets resource resolver service...");
        portalFaceletsResourceResolverService = null;
    }

    public FaceletsResourceResolverService getPortalFaceletsResourceResolverService() {
        return portalFaceletsResourceResolverService;
    }

    public static PortalFaceletsResourceResolverServiceConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PortalFaceletsResourceResolverServiceConsumer();
        }
        return INSTANCE;
    }
}