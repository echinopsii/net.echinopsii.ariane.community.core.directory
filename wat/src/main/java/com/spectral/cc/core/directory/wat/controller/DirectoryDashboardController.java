/**
 * Directory Commons JSF bundle
 * Directories Dashboard Controller
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

package com.spectral.cc.core.directory.wat.controller;

import com.spectral.cc.core.directory.wat.consumer.DirectoryTreeMenuRootsRegistryServiceConsumer;
import com.spectral.cc.core.portal.base.model.TreeMenuEntity;
import com.spectral.cc.core.portal.base.model.MenuEntityType;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.primefaces.component.dashboard.Dashboard;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Directory dashboard controller transform directory roots registry into primefaces DashboardModel to populate the directory main view
 */
public class DirectoryDashboardController {
    private static final Logger log = LoggerFactory.getLogger(DirectoryDashboardController.class);

    private HashMap<String, DashboardColumn> columnHashMap = new HashMap<String, DashboardColumn>();
    private DashboardModel                   model         = new DefaultDashboardModel();
    private Dashboard                        dashboard;

    private static boolean isAuthorized(Subject subject, TreeMenuEntity entity) {
        boolean ret = false;
        if (subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone") || entity.getDisplayRoles().size()==0) {
            ret = true;
        } else {
            for (String role : entity.getDisplayRoles())
                if (subject.hasRole(role)) {
                    ret = true;
                    break;
                }
            if (!ret) {
                for (String perm : entity.getDisplayPermissions())
                    if (subject.isPermitted(perm)) {
                        ret = true;
                        break;
                    }
            }
        }
        return ret;
    }

    private void rootCreateRootSubmenuWidget(Subject subject, TreeMenuEntity curEntity, String curTitle, DefaultDashboardColumn lastColumn) {
        DefaultDashboardColumn curColumn = lastColumn;
        for (TreeMenuEntity child : curEntity.getChildTreeMenuEntities()) {
            String nextTitle = curTitle+" / "+child.getValue();
            switch (child.getType()) {
                case MenuEntityType.TYPE_MENU_ITEM:
                    if (isAuthorized(subject, child)) {
                        if (curColumn==null) {
                            curColumn = new DefaultDashboardColumn();
                            model.addColumn(curColumn);
                        }
                        log.debug("Add widget {} to column {} ...", new Object[]{nextTitle, curTitle});
                        curColumn.addWidget(nextTitle);
                    }
                    break;
                case MenuEntityType.TYPE_MENU_SUBMENU:
                    if (isAuthorized(subject, child)) {
                        rootCreateRootSubmenuWidget(subject, child, nextTitle, curColumn);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public DirectoryDashboardController() {
        log.debug("Init Dashboard Model...");
        Subject subject = SecurityUtils.getSubject();
        if (DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance()!=null) {
            DefaultDashboardColumn lonlyItemColumn = new DefaultDashboardColumn();
            model.addColumn(lonlyItemColumn);
            for (TreeMenuEntity entity : DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuRootsEntities()) {
                switch (entity.getType()) {
                    case MenuEntityType.TYPE_MENU_ITEM:
                        if (isAuthorized(subject, entity)) {
                            lonlyItemColumn.addWidget(entity.getValue());
                        }
                        break;
                    case MenuEntityType.TYPE_MENU_SUBMENU:
                        if (isAuthorized(subject, entity)) {
                            rootCreateRootSubmenuWidget(subject, entity, entity.getValue(), null);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public DashboardModel getModel() {
        log.debug("Get Dashboard Model...");
        return model;
    }

    public List<DashboardColumn> getDashboardColumn() {
        log.debug("Get Dashboard Columns...");
        ArrayList<DashboardColumn> ret = new ArrayList<>();
        for (DashboardColumn column : model.getColumns()) {
            if (column.getWidgets().size()!=0)
                ret.add(column);
        }
        return ret;
    }

    public String getColumnName(DashboardColumn column) {
        String ret = "Directories ";
        String titleParts[] = column.getWidget(0).split(" / ");
        for (int i = 0; i<titleParts.length-1; i++) {
            ret += " / " +titleParts[i];
        }
        return ret;
    }

    public List<String> getColumnWidgets(DashboardColumn column) {
        if (column!=null) {
            log.debug("Get Dashboard widgets from column {}...", new Object[]{column.toString()});
            return column.getWidgets();
        } else return new ArrayList<String>();
    }

    private String getDirectoryValueFromWidgetName(String widgetName) {
        String[] table = widgetName.split(" / ");
        return table[table.length-1];
    }

    public String getWidgetValue(String widgetName) {
        String ret = "";
        TreeMenuEntity entity = DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuEntityFromValue(getDirectoryValueFromWidgetName(widgetName));
        if (entity!=null)
            ret = entity.getValue();
        log.debug("Get Value from widget {} : {}...", new Object[]{widgetName,ret});
        return ret;
    }

    public String getWidgetDescription(String widgetName) {
        String ret = "";
        TreeMenuEntity entity = DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuEntityFromValue(getDirectoryValueFromWidgetName(widgetName));
        if (entity!=null)
            ret = entity.getDescription();
        log.debug("Get description from widget {} : {}...", new Object[]{widgetName,ret});
        return ret;
    }

    public String getWidgetIcon(String widgetName) {
        String ret = "";
        TreeMenuEntity entity = DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuEntityFromValue(getDirectoryValueFromWidgetName(widgetName));
        if (entity!=null)
            ret = entity.getIcon() + " icon-4x";
        log.debug("Get icon from widget {} : {}...", new Object[]{widgetName, ret});
        return ret;
    }

    public String getWidgetAddress(String widgetName) {
        String ret = "";
        FacesContext context = FacesContext.getCurrentInstance();
        TreeMenuEntity entity = DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuEntityFromValue(getDirectoryValueFromWidgetName(widgetName));
        if (entity!=null)
            ret = context.getExternalContext().getRequestScheme() + "://" +
                          context.getExternalContext().getRequestServerName() + ":" +
                          context.getExternalContext().getRequestServerPort() +
                          entity.getContextAddress();
        log.debug("Get address from widget {} : {}...", new Object[]{widgetName,ret});
        return ret ;
    }
}