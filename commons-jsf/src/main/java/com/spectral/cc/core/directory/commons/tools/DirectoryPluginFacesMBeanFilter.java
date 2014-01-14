/**
 * Directory Commons JSF bundle
 * Plugin Faces Managed Bean Filter
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
package com.spectral.cc.core.directory.commons.tools;

import com.spectral.cc.core.directory.commons.consumer.DirectoryPluginFacesMBeanRegistryConsumer;

import javax.servlet.*;
import java.io.IOException;

public class DirectoryPluginFacesMBeanFilter implements Filter {

    /**
     * The filter configuration object we are associated with. If this value is null, this filter instance is not currently
     * configured.
     */
    protected FilterConfig filterConfig = null;

    public void destroy() {
        this.filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
        DirectoryPluginFacesMBeanRegistryConsumer.getInstance().getDirectoryPluginFacesMBeanRegistry().addPluginFacesMBeanConfigsToServletContext();

        // Pass control on to the next filter
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        while(DirectoryPluginFacesMBeanRegistryConsumer.getInstance().getDirectoryPluginFacesMBeanRegistry()==null)
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        DirectoryPluginFacesMBeanRegistryConsumer.getInstance().getDirectoryPluginFacesMBeanRegistry().registerServletContext(filterConfig.getServletContext());
    }
}
