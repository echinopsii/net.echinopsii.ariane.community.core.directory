/**
 * Directory base
 * Model organisational/company
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
package com.spectral.cc.core.directory.base.model.organisational;

import com.spectral.cc.core.directory.base.model.technical.system.OSType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="company",uniqueConstraints = @UniqueConstraint(columnNames = {"companyName"}))
public class Company implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="companyName",unique=true)
    @NotNull
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @XmlTransient
    private Set<Application> applications = new HashSet<Application>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @XmlTransient
    private Set<OSType> osTypes = new HashSet<OSType>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Company setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public Company setVersionR(final int version) {
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
            return id.equals(((Company) that).id);
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

    public Company setNameR(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Company setDescriptionR(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        if (description != null && !description.trim().isEmpty())
            result += ", description: " + description;
        return result;
    }

    @XmlTransient
    public Set<Application> getApplications() {
        return this.applications;
    }

    public void setApplications(final Set<Application> applications) {
        this.applications = applications;
    }

    public Company setApplicationsR(final Set<Application> applications) {
        this.applications = applications;
        return this;
    }

    @XmlTransient
    public Set<OSType> getOsTypes() {
        return osTypes;
    }

    public void setOsTypes(Set<OSType> osTypes) {
        this.osTypes = osTypes;
    }

    public Company setOsTypesR(Set<OSType> osTypes) {
        this.osTypes = osTypes;
        return this;
    }

    public Company clone() {
        return new Company().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setDescriptionR(this.description).
                             setOsTypesR(new HashSet(this.osTypes)).setApplicationsR(new HashSet(this.applications));
    }
}