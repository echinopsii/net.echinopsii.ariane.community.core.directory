/**
 * Injector Commons JSF bundle
 * Facelets Resource Resolver
 * Copyright (C) 2013 Mathilde Ffrench
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 */
package com.spectral.cc.core.directory.commons.tools;

import com.spectral.cc.core.directory.commons.consumer.DirectoryFaceletsResourceResolverServiceConsumer;
import com.spectral.cc.core.portal.commons.facesplugin.FaceletsResourceResolverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.view.facelets.ResourceResolver;
import java.net.URL;

/**
 * Helper to find facelets resource. First it check on the directory main, then on the directory commons-jsf bundle and finally ask any
 * Facelets Resource Resolver Service existing in the container... <br/><br/>
 *
 * To use this resolver you must have following web.xml configuration :
 * <pre>
 *          <context-param>
 *              <param-name>javax.faces.FACELETS_RESOURCE_RESOLVER</param-name>
 *              <param-value>com.spectral.cc.core.directory.commons.tools.FaceletsResourceResolver</param-value>
 *          </context-param>
 * </pre>
 */
public class FaceletsResourceResolver extends ResourceResolver {

    private static final Logger log = LoggerFactory.getLogger(FaceletsResourceResolver.class);
    private ResourceResolver parent;
    private String basePath;

    public FaceletsResourceResolver(ResourceResolver parent) {
        this.parent = parent;
        this.basePath = "/META-INF";
    }

    /**
     * Resource resolver implementation.
     * First it check on the direcoty main, then on the directory commons-jsf bundle and finally ask any Facelets Resource Resolver Service existing in the container...
     * @param path
     * @return
     */
    @Override
    public URL resolveUrl(String path) {
        log.debug("Resolve {} from directory main...", new Object[]{path});
        URL url = parent.resolveUrl(path);

        if (url == null) {
            log.debug("Resolve {} from directory commons-jsf...", new Object[]{path});
            url = FaceletsResourceResolver.class.getResource(basePath + path);
        }

        if (url == null &&
            DirectoryFaceletsResourceResolverServiceConsumer.getInstance()!=null &&
            DirectoryFaceletsResourceResolverServiceConsumer.getInstance().getFaceletsResourceResolverServices()!=null) {
            for (FaceletsResourceResolverService fResolver : DirectoryFaceletsResourceResolverServiceConsumer.getInstance().getFaceletsResourceResolverServices()) {
                log.debug("Resolve {} from face resolver from package {}...", new Object[]{path, fResolver.getClass().getPackage()});
                url = fResolver.resolveURL(path);
                if (url!=null)
                    break;
            }
        }

        return url;
    }
}
