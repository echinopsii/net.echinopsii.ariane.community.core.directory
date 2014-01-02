/**
 * Directory Main bundle
 * Directories OSType Entity
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
package com.spectral.cc.core.directory.commons.model.technical.system;

import com.spectral.cc.core.directory.commons.model.organisational.Company;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="osType", uniqueConstraints = @UniqueConstraint(columnNames = {"osTypeName"}))
public class OSType implements Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="osTypeName",unique=true)
    @NotNull
    private String name;

    @Column
    private String architecture;

    @ManyToOne
    private Company company;

    @OneToMany(mappedBy = "osType", cascade = CascadeType.ALL)
    private Set<OSInstance> osInstances = new HashSet<OSInstance>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public OSType setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public OSType setVersionR(final int version) {
        this.version = version ;
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
            return id.equals(((OSType) that).id);
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

    public OSType setNameR(final String name) {
        this.name = name ;
        return this;
    }

    public String getArchitecture() {
        return this.architecture;
    }

    public void setArchitecture(final String architecture) {
        this.architecture = architecture;
    }

    public OSType setArchitectureR(final String architecture) {
        this.architecture = architecture;
        return this;
    }

    public Company getCompany() {
        return this.company;
    }

    public void setCompany(final Company company) {
        this.company = company;
    }

    public OSType setCompanyR(final Company company) {
        this.company = company;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        if (architecture != null && !architecture.trim().isEmpty())
            result += ", architecture: " + architecture;
        return result;
    }

    public Set<OSInstance> getOsInstances() {
        return this.osInstances;
    }

    public void setOsInstances(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
    }

    public OSType setOsInstancesR(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
        return this;
    }

    public OSType clone() {
        return new OSType().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setArchitectureR(this.architecture).setCompanyR(this.company).
                            setOsInstancesR(new HashSet<OSInstance>(this.osInstances));
    }
}