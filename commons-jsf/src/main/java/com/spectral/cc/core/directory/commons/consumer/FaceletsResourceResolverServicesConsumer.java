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

import com.spectral.cc.core.portal.commons.fresolver.FaceletsResourceResolverService;
import org.apache.felix.ipojo.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@Component(publicFactory = false, factoryMethod = "getInstance")
@Instantiate
public class FaceletsResourceResolverServicesConsumer {
    private static final Logger log = LoggerFactory.getLogger(FaceletsResourceResolverServicesConsumer.class);
    private static FaceletsResourceResolverServicesConsumer INSTANCE;

    private ArrayList<FaceletsResourceResolverService> faceletsResolverList = new ArrayList<FaceletsResourceResolverService>();

    @Bind
    public void bindFaceletsResourceResolverService(FaceletsResourceResolverService r) {
        log.debug("Consumer bound to facelets resource resolver service from package {}...", new Object[]{r.getClass().getPackage()});
        faceletsResolverList.add(r);
    }

    @Unbind
    public void unbindFaceletsResourceResolverService(FaceletsResourceResolverService r) {
        log.debug("Consumer unbound from facelets resource resolver service from package {}...", new Object[]{r.getClass().getPackage()});
        faceletsResolverList.remove(r);
    }

    public ArrayList<FaceletsResourceResolverService> getFaceletsResourceResolverServices() {
        return faceletsResolverList;
    }

    public static FaceletsResourceResolverServicesConsumer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new FaceletsResourceResolverServicesConsumer();
        }
        return INSTANCE;
    }
}