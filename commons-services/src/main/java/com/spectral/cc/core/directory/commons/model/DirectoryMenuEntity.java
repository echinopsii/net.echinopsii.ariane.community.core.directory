/**
 * Directory Commons Services bundle
 * Directory Entity
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

package com.spectral.cc.core.directory.commons.model;

import com.spectral.cc.core.portal.commons.model.MenuEntityType;

import java.util.TreeSet;

/**
 * A Directory Menu Entity represent a directory menu entry with its child. Used by directory dashboard controller, directory menu controller and
 * directory bread crum controller.
 */
public class DirectoryMenuEntity implements Comparable<DirectoryMenuEntity> {

    private String id             = null;
    private String value          = null;
    private int    type           = 0;
    private String contextAddress = "";
    private String description    = "";
    private String icon           = "";

    private DirectoryMenuEntity parent = null;
    private TreeSet<DirectoryMenuEntity> childs = new TreeSet<DirectoryMenuEntity>();

    /**
     * Get directory menu entity id
     *
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the directory menu entity id.<br/><br/>
     *
     * The id must be unique and must follow the following rule to make work the breadscrum :
     * if the context address is something like views/myview.xhtml then the id must be defined as follow : myviewDirID
     *
     * @param id the directory menu entity
     *
     * @return this directory menu entity
     */
    public DirectoryMenuEntity setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Set the directory menu entity value (display of the entity in primefaces menu rendering). The value must be unique.
     *
     * @param value
     *
     * @return this directory menu entity
     */
    public DirectoryMenuEntity setValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * Get the directory menu entity value (display of the entity in primefaces menu rendering)
     *
     * @return the directory menu entity
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the type of the directory menu entity.
     *
     * @return directory menu entity type
     */
    public int getType() {
        return type;
    }

    /**
     * Set the type of the directory menu entity.
     *
     * @param type ot the directory menu entity. Supported type are defined here in {@link MenuEntityType}.
     *
     * @return this directory menu entity type
     *
     * @throws Exception if provided type is not supported.
     */
    public DirectoryMenuEntity setType(int type) throws Exception {
        if (type == MenuEntityType.TYPE_MENU_ITEM || type == MenuEntityType.TYPE_MENU_SUBMENU || type == MenuEntityType.TYPE_MENU_SEPARATOR)
            this.type = type;
        else
            throw new Exception("Not supported directory entity type : " + type);
        return this;
    }

    /**
     * Get the directory menu entity target context address
     *
     * @return the context address
     */
    public String getContextAddress() {
        return contextAddress;
    }

    /**
     * Set the directory menu entity context address
     *
     * @param contextAddress
     *
     * @return this directory menu entity type
     */
    public DirectoryMenuEntity setContextAddress(String contextAddress) {
        this.contextAddress = contextAddress;
        return this;
    }

    /**
     * Set the directory menu entity description
     *
     * @return the directory menu entity description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the directory menu entity description (displayed in the directory dashboard)
     *
     * @param description
     *
     * @return this directory menu entity
     */
    public DirectoryMenuEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Get icon of this directory menu entity
     *
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Set icon of this directory menu entity (could be jquery-ui icon or font awesome icon)
     *
     * @param icon
     *
     * @return this directory menu entity
     */
    public DirectoryMenuEntity setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    /**
     * Set the directory menu entity parent
     *
     * @param parent
     *
     * @return this directory menu entity
     */
    public DirectoryMenuEntity setParentDirectory(DirectoryMenuEntity parent) {
        this.parent = parent;
        return this;
    }

    /**
     * Get the directory menu entity parent
     *
     * @return the directory menu entity parent
     */
    public DirectoryMenuEntity getParentDirectory() {
        return parent;
    }

    /**
     * Add the directory menu entity child
     *
     * @param child
     *
     * @return this directory menu entity
     */
    public DirectoryMenuEntity addChildDirectory(DirectoryMenuEntity child) {
        this.childs.add(child);
        return this;
    }

    /**
     * Get the directory menu entity childs
     *
     * @return the directory menu entity childs
     */
    public TreeSet<DirectoryMenuEntity> getChildsDirectory() {
        return this.childs;
    }

    /**
     * Find and return directory menu entity from value
     *
     * @param value_
     *
     * @return the found directory menu entity or null if no entity has been found
     */
    public DirectoryMenuEntity findDirectoryMenuEntityFromValue(String value_) {
        DirectoryMenuEntity ret = null;
        if (this.value.equals(value_)) {
            ret = this;
        } else {
            for (DirectoryMenuEntity entity : childs) {
                ret = entity.findDirectoryMenuEntityFromValue(value_);
                if (ret!=null) break;
            }
        }
        return ret;
    }

    /**
     * Find and return directory menu entity from id
     *
     * @param id_
     *
     * @return the found directory menu entity or null if no entity has been found
     */
    public DirectoryMenuEntity findDirectoryMenuEntityFromID(String id_) {
        DirectoryMenuEntity ret = null;
        if (this.id.equals(id_)) {
            ret = this;
        } else {
            for (DirectoryMenuEntity entity : childs) {
                ret = entity.findDirectoryMenuEntityFromID(id_);
                if (ret!=null) break;
            }
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DirectoryMenuEntity that = (DirectoryMenuEntity) o;

        if (!id.equals(that.id)) {
            return false;
        }
        if (!value.equals(that.value)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DirectoryMenuEntity{" +
                       "id='" + id + '\'' +
                       ", value='" + value + '\'' +
                       '}';
    }

    @Override
    public int compareTo(DirectoryMenuEntity that) {
        return this.value.compareTo(that.getValue());
    }
}