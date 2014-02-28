/**
 * Directory Commons JSF bundle
 * Directories Menu Controller
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
import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.separator.Separator;
import org.primefaces.component.submenu.Submenu;
import org.primefaces.model.DefaultMenuModel;
import org.primefaces.model.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.FacesContext;

/**
 * Directory menu controller transform directory roots registry into primefaces menu model to be used in directory layout panel menu component <br/>
 * This is a request managed bean
 */
public class DirectoryMenuController {
    private static final Logger log = LoggerFactory.getLogger(DirectoryMenuController.class);

    private MenuModel model     = new DefaultMenuModel();

    private MenuItem createMenuItemFromEntity(TreeMenuEntity entity) {
        FacesContext context = FacesContext.getCurrentInstance();
        MenuItem item = new MenuItem();
        item.setId(entity.getId());
        item.setUrl(context.getExternalContext().getRequestScheme() + "://" +
                            context.getExternalContext().getRequestServerName() + ":" +
                            context.getExternalContext().getRequestServerPort() +
                            entity.getContextAddress());
        item.setIcon(entity.getIcon() + " icon-large");
        item.setValue(entity.getValue());
        item.setStyleClass("menuItem");

        return item;
    }

    private Submenu createSubMenuFromEntity(TreeMenuEntity entity) {
        Submenu submenu = new Submenu();
        submenu.setId(entity.getId());
        submenu.setStyleClass("menuItem");
        submenu.setLabel(entity.getValue());
        submenu.setIcon(entity.getIcon() + " icon-large");
        for (TreeMenuEntity subEntity : entity.getChildTreeMenuEntities()) {
            switch(subEntity.getType()) {
                case MenuEntityType.TYPE_MENU_SUBMENU:
                    Submenu subSubMenu = createSubMenuFromEntity(subEntity);
                    submenu.getChildren().add(subSubMenu);
                    break;
                case MenuEntityType.TYPE_MENU_ITEM:
                    MenuItem item = createMenuItemFromEntity(subEntity);
                    submenu.getChildren().add(item);
                    break;
                case MenuEntityType.TYPE_MENU_SEPARATOR:
                    Separator separator = new Separator();
                    separator.setId(subEntity.getId());
                    submenu.getChildren().add(separator);
                    break;
                default:
                    break;
            }
        }
        return submenu;
    }

    public MenuModel getModel() {
        log.debug("Get Menu Model...");
        if (DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance()!=null) {
            for (TreeMenuEntity entity : DirectoryTreeMenuRootsRegistryServiceConsumer.getInstance().getTreeMenuRootsRegistry().getTreeMenuRootsEntities()) {
                switch (entity.getType()) {
                    case MenuEntityType.TYPE_MENU_ITEM:
                        MenuItem item = createMenuItemFromEntity(entity);
                        model.addMenuItem(item);
                        break;
                    case MenuEntityType.TYPE_MENU_SUBMENU:
                        Submenu submenu = createSubMenuFromEntity(entity);
                        model.addSubmenu(submenu);
                        break;
                    default:
                        break;
                }
            }
        }
        return model;
    }
}