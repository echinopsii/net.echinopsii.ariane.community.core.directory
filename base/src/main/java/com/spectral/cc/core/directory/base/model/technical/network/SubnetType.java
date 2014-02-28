/**
 * Directory Main bundle
 * Directories SubnetType Entity
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
package com.spectral.cc.core.directory.base.model.technical.network;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="subnetType",uniqueConstraints = @UniqueConstraint(columnNames = {"subnetTypeName"}))
public class SubnetType implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="subnetTypeName",unique=true)
    @NotNull
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private Set<Subnet> subnets = new HashSet<Subnet>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public SubnetType setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public SubnetType setVersionR(final int version) {
        this.version = version;
        return this;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        if (id != null) {
            return id.equals(((SubnetType) that).id);
        }
        return super.equals(that);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public SubnetType setNameR(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SubnetType setDescriptionR(String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        return result;
    }

    public Set<Subnet> getSubnets() {
        return this.subnets;
    }

    public void setSubnets(final Set<Subnet> subnets) {
        this.subnets = subnets;
    }

    public SubnetType setSubnetsR(final Set<Subnet> subnets) {
        this.subnets = subnets;
        return this;
    }

    @Override
    public SubnetType clone () {
        return new SubnetType().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setDescriptionR(this.description).setSubnetsR(new HashSet<Subnet>(this.subnets));
    }
}