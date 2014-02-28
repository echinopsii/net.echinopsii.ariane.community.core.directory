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

    private void rootCreateRootSubmenuWidget(TreeMenuEntity curEntity, String curTitle, DefaultDashboardColumn lastColumn) {
        DefaultDashboardColumn curColumn = lastColumn;
        for (TreeMenuEntity child : curEntity.getChildTreeMenuEntities()) {
            String nextTitle = curTitle+" / "+child.getValue();
            switch (child.getType()) {
                case MenuEntityType.TYPE_MENU_ITEM:
                    if (curColumn==null) {
                        curColumn = new DefaultDashboardColumn();
                        model.addColumn(curColumn);
                    }
                    log.debug("Add widget {} to column {} ...", new Object[]{nextTitle, curTitle});
                    curColumn.addWidget(nextTitle);
                    break;
                case MenuEntityType.TYPE_MENU_SUBMENU:
                    rootCreateRootSubmenuWidget(child, nextTitle, curColumn);
                    break;
                default:
                    break;
            }
        }
    }

    //TODO: RETRY !
/*
    @PostConstruct
    private void init() {
        FacesContext fc          = FacesContext.getCurrentInstance();
        Application  application = fc.getApplication();
        dashboard = (Dashboard) application.createComponent(fc, "org.primefaces.component.Dashboard", "org.primefaces.component.DashboardRenderer");
        dashboard.setId("dashboard");

        for (DashboardColumn column : model.getColumns()) {
            for (String widget : column.getWidgets()) {
                Panel panel = (Panel) application.createComponent(fc, "org.primefaces.component.Panel", "org.primefaces.component.PanelRenderer");
                //panel.setId("p"+widget);
                panel.setHeader(widget);
                panel.setClosable(true);
                panel.setToggleable(true);

                dashboard.getChildren().add(panel);
                column.addWidget(panel.getId());
                HtmlOutputText text = new HtmlOutputText();
                //text.setId("t"+widget);
                text.setValue(getWidgetDescription(widget));

                panel.getChildren().add(text);
            }
        }
    }
*/

    public DirectoryDashboardController() {
        log.debug("Init Dashboard Model...");
        if (DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance()!=null) {
            DefaultDashboardColumn lonlyItemColumn = new DefaultDashboardColumn();
            model.addColumn(lonlyItemColumn);
            for (TreeMenuEntity entity : DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuRootsEntities()) {
                switch (entity.getType()) {
                    case MenuEntityType.TYPE_MENU_ITEM:
                        lonlyItemColumn.addWidget(entity.getValue());
                        break;
                    case MenuEntityType.TYPE_MENU_SUBMENU:
                        rootCreateRootSubmenuWidget(entity, entity.getValue(), null);
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