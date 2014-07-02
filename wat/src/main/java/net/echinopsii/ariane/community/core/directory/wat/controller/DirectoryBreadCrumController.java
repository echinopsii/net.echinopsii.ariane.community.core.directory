/**
 * Directory wat
 * Directories BreadCrum Controller
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
package net.echinopsii.ariane.community.core.directory.wat.controller;

import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryFacesMBeanRegistryConsumer;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryTreeMenuRootsRegistryServiceConsumer;
import net.echinopsii.ariane.community.core.portal.base.model.TreeMenuEntity;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.util.ArrayList;

/**
 * Directory bread crum controller transform directory tree menu roots registry entities into primefaces menu model to be used in directory layout bread crum component <br/>
 * This is a request managed bean
 */
public class DirectoryBreadCrumController {
    private static final Logger log = LoggerFactory.getLogger(DirectoryBreadCrumController.class);
    private static String MAIN_MENU_DIRECTORY_CONTEXT = DirectoryFacesMBeanRegistryConsumer.getInstance().getPortalPluginFacesMBeanRegistry().getRegisteredServletContext().getContextPath();

    private MenuModel model     = new DefaultMenuModel();

    private MenuItem createMenuItemFromEntity(TreeMenuEntity entity) {
        FacesContext context = FacesContext.getCurrentInstance();
        MenuItem item = new MenuItem();
        item.setId(entity.getId());
        if (entity.getContextAddress()!=null && entity.getContextAddress()!="")
            item.setUrl(context.getExternalContext().getRequestScheme() + "://" +
                                context.getExternalContext().getRequestServerName() + ":" +
                                context.getExternalContext().getRequestServerPort() +
                                entity.getContextAddress());
        else
            item.setUrl(context.getExternalContext().getRequestScheme() + "://" +
                                context.getExternalContext().getRequestServerName() + ":" +
                                context.getExternalContext().getRequestServerPort() +
                                MAIN_MENU_DIRECTORY_CONTEXT + "/views/main.jsf");
        item.setValue(entity.getValue());
        item.setStyleClass("menuItem");

        return item;
    }

    private MenuItem createRootMenuItem() {
        FacesContext context = FacesContext.getCurrentInstance();
        MenuItem item = new MenuItem();
        item.setId("mainDir");
        item.setUrl(context.getExternalContext().getRequestScheme() + "://" +
                            context.getExternalContext().getRequestServerName() + ":" +
                            context.getExternalContext().getRequestServerPort() +
                            MAIN_MENU_DIRECTORY_CONTEXT + "/views/main.jsf");
        item.setValue("Directory");
        item.setStyleClass("menuItem");
        return item;
    }

    public MenuModel getModel() {
        FacesContext context = FacesContext.getCurrentInstance();
        String contextAddress = MAIN_MENU_DIRECTORY_CONTEXT+ context.getExternalContext().getRequestServletPath();
        ArrayList<TreeMenuEntity> orderedBreadScrumMenuFromRootToLeaf = new ArrayList<TreeMenuEntity>();
        if (DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance()!=null) {
            TreeMenuEntity leaf   = DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuEntityFromContextAddress(contextAddress);
            if (leaf!=null) {
                orderedBreadScrumMenuFromRootToLeaf.add(0,leaf);
                TreeMenuEntity parent = leaf.getParentTreeMenuEntity();
                while (parent!=null) {
                    orderedBreadScrumMenuFromRootToLeaf.add(0,parent);
                    parent = parent.getParentTreeMenuEntity();
                }

                model.addMenuItem(createRootMenuItem());
                for (TreeMenuEntity dir: orderedBreadScrumMenuFromRootToLeaf)
                    model.addMenuItem(createMenuItemFromEntity(dir));
            }
        }
        return model;
    }

}