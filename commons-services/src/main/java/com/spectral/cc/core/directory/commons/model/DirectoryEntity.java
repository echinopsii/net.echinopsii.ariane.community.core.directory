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

import java.util.TreeSet;

public class DirectoryEntity implements Comparable<DirectoryEntity> {

    private String id             = null;
    private String value          = null;
    private int    type           = 0;
    private String contextAddress = "";
    private String description    = "";
    private String icon           = "";

    private DirectoryEntity          parent = null;
    private TreeSet<DirectoryEntity> childs = new TreeSet<DirectoryEntity>();

    public String getId() {
        return id;
    }

    public DirectoryEntity setId(String id) {
        this.id = id;
        return this;
    }

    public DirectoryEntity setValue(String value) {
        this.value = value;
        return this;
    }

    public String getValue() {
        return value;
    }

    public int getType() {
        return type;
    }

    public DirectoryEntity setType(int type) {
        this.type = type;
        return this;
    }

    public String getContextAddress() {
        return contextAddress;
    }

    public DirectoryEntity setContextAddress(String contextAddress) {
        this.contextAddress = contextAddress;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DirectoryEntity setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getIcon() {
        return icon;
    }

    public DirectoryEntity setIcon(String icon) {
        this.icon = icon;
        return this;
    }

    public DirectoryEntity setParentDirectory(DirectoryEntity parent) {
        this.parent = parent;
        return this;
    }

    public DirectoryEntity getParentDirectory() {
        return parent;
    }

    public DirectoryEntity addChildDirectory(DirectoryEntity child) {
        this.childs.add(child);
        return this;
    }

    public TreeSet<DirectoryEntity> getChildsDirectory() {
        return this.childs;
    }

    public DirectoryEntity findDirectoryEntityFromValue(String value_) {
        DirectoryEntity ret = null;
        if (this.value.equals(value_)) {
            ret = this;
        } else {
            for (DirectoryEntity entity : childs) {
                ret = entity.findDirectoryEntityFromValue(value_);
                if (ret!=null)
                    break;
            }
        }
        return ret;
    }

    public DirectoryEntity findDirectoryEntityFromID(String id_) {
        DirectoryEntity ret = null;
        if (this.id.equals(id_)) {
            ret = this;
        } else {
            for (DirectoryEntity entity : childs) {
                ret = entity.findDirectoryEntityFromID(id_);
                if (ret!=null)
                    break;
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

        DirectoryEntity that = (DirectoryEntity) o;

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
        return "DirectoryEntity{" +
                       "id='" + id + '\'' +
                       ", value='" + value + '\'' +
                       '}';
    }

    @Override
    public int compareTo(DirectoryEntity that) {
        return this.value.compareTo(that.getValue());
    }
}