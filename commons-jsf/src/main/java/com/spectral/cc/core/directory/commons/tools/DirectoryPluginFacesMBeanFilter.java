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

/**
 * This servlet filter is an helper to add new Managed Bean coming from CC plugin to the directory servlet context thanks the directory plugin faces mbean registry consumer.<br/>
 * It must be configured properly in the web.xml file :<br/><br/>
 * <pre>
 *         <!-- Directory Plugin Faces Managed Bean Registry Filter -->
 *         <filter>
 *              <filter-name>DirectoryPluginFacesMBeanRegistryFilter</filter-name>
 *              <filter-class>com.spectral.cc.core.directory.commons.tools.DirectoryPluginFacesMBeanFilter</filter-class>
 *         </filter>
 *         <filter-mapping>
 *              <filter-name>DirectoryPluginFacesMBeanRegistryFilter</filter-name>
 *              <url-pattern>*.jsf</url-pattern>
 *         </filter-mapping>
 * </pre>
 */
public class DirectoryPluginFacesMBeanFilter implements Filter {

    /**
     * The filter configuration object we are associated with. If this value is null, this filter instance is not currently
     * configured.
     */
    protected FilterConfig filterConfig = null;

    public void destroy() {
        this.filterConfig = null;
    }

    /**
     * Ask the directory plugin faces managed bean registry to add registers faces managed bean to the directory servlet context,
     * and then pass control to the next filter
     *
     * @param request
     * @param response
     * @param chain
     *
     * @throws IOException
     * @throws ServletException
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {
        DirectoryPluginFacesMBeanRegistryConsumer.getInstance().getDirectoryPluginFacesMBeanRegistry().addPluginFacesMBeanConfigsToServletContext();
        // pass control on to the next filter
        chain.doFilter(request, response);
    }

    /**
     * Register the directory servlet context into the directory plugin faces managed bean registry
     *
     * @param filterConfig
     *
     * @throws ServletException
     */
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
