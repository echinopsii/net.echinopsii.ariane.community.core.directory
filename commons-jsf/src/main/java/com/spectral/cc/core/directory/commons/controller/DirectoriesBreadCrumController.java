/**
 * Directory Commons JSF bundle
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
package com.spectral.cc.core.directory.commons.controller;

import com.spectral.cc.core.directory.commons.consumer.DirectoryRootsTreeRegistryServiceConsumer;
import com.spectral.cc.core.directory.commons.model.DirectoryEntity;
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;
import java.util.ArrayList;

public class DirectoriesBreadCrumController {
    private static final Logger log = LoggerFactory.getLogger(DirectoriesBreadCrumController.class);
    private static String MAIN_MENU_DIRECTORY_CONTEXT = "/CCdirectory/";

    private MenuModel model     = new DefaultMenuModel();

    private MenuItem createMenuItemFromEntity(DirectoryEntity entity) {
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
                                MAIN_MENU_DIRECTORY_CONTEXT + "views/main.jsf");
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
                            MAIN_MENU_DIRECTORY_CONTEXT + "views/main.jsf");
        item.setValue("Directory");
        item.setStyleClass("menuItem");
        return item;
    }

    public MenuModel getModel() {
        FacesContext context = FacesContext.getCurrentInstance();
        String requestServletPath = context.getExternalContext().getRequestServletPath();
        String values[] = requestServletPath.split("/");
        String id = values[values.length-1].split("\\.")[0]+"DirID" ;
        log.debug("requestServletPath : {} ; value : {}", new Object[]{requestServletPath,id});
        log.debug("Get Menu Model...");
        ArrayList<DirectoryEntity> orderedBreadScrumMenuFromRootToLeaf = new ArrayList<DirectoryEntity>();
        if (DirectoryRootsTreeRegistryServiceConsumer.getInstance()!=null) {
            DirectoryEntity leaf   = DirectoryRootsTreeRegistryServiceConsumer.getInstance().getDirectoryRootsTreeRegistry().getDirectoryEntityFromID(id);
            if (leaf!=null) {
                orderedBreadScrumMenuFromRootToLeaf.add(0,leaf);
                DirectoryEntity parent = leaf.getParentDirectory();
                while (parent!=null) {
                    orderedBreadScrumMenuFromRootToLeaf.add(0,parent);
                    parent = parent.getParentDirectory();
                }

                model.addMenuItem(createRootMenuItem());
                for (DirectoryEntity dir: orderedBreadScrumMenuFromRootToLeaf)
                    model.addMenuItem(createMenuItemFromEntity(dir));
            }
        }
        return model;
    }

}