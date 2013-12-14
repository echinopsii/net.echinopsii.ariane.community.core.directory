/**
 * Directory Main bundle
 * Directories Lan Entity
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
package com.spectral.cc.core.directory.main.model.technical.network;

import com.spectral.cc.core.directory.main.model.technical.system.OSInstance;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="lan", uniqueConstraints = @UniqueConstraint(columnNames = {"lanName"}))
public class Lan implements Serializable
{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="lanName", unique=true)
    @NotNull
    private String name;

    @Column
    private String description;

    @Column
    @NotNull
    private String subnetIP;

    @Column
    @NotNull
    private String subnetMask;

    @ManyToMany
    private Set<OSInstance> osInstances = new HashSet<OSInstance>();

    @ManyToMany(mappedBy = "lans")
    private Set<Datacenter> datacenters = new HashSet<Datacenter>();

    @ManyToOne
    private LanType type;

    @ManyToOne
    private MulticastArea marea;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Lan setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public Lan setVersionR(final int version) {
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
            return id.equals(((Lan) that).id);
        }
        return super.equals(that);
    }

    @Override
    public int hashCode()
    {
        if (id != null)
        {
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

    public Lan setNameR(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public Lan setDescriptionR (String description) {
        this.description = description;
        return this;
    }

    public String getSubnetIP() {
        return this.subnetIP;
    }

    public void setSubnetIP(final String subnetIP) {
        this.subnetIP = subnetIP;
    }

    public Lan setSubnetIPR(final String subnetIP) {
        this.subnetIP = subnetIP;
        return this;
    }

    public String getSubnetMask() {
        return this.subnetMask;
    }

    public void setSubnetMask(final String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public Lan setSubnetMaskR(final String subnetMask) {
        this.subnetMask = subnetMask;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        if (subnetIP != null && !subnetIP.trim().isEmpty())
            result += ", subnetIP: " + subnetIP;
        if (subnetMask != null && !subnetMask.trim().isEmpty())
            result += ", subnetMask: " + subnetMask;
        return result;
    }

    public Set<OSInstance> getOsInstances() {
        return this.osInstances;
    }

    public void setOsInstances(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
    }

    public Lan setOsInstancesR(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
        return this;
    }

    public Set<Datacenter> getDatacenters() {
        return this.datacenters;
    }

    public void setDatacenters(final Set<Datacenter> datacenters) {
        this.datacenters = datacenters;
    }

    public Lan setDatacentersR(final Set<Datacenter> datacenters) {
        this.datacenters = datacenters;
        return this;
    }

    public LanType getType() {
        return this.type;
    }

    public void setType(final LanType type) {
        this.type = type;
    }

    public Lan setTypeR(final LanType type) {
        this.type = type;
        return this;
    }

    public MulticastArea getMarea() {
        return this.marea;
    }

    public void setMarea(final MulticastArea marea) {
        this.marea = marea;
    }

    public Lan setMareaR(final MulticastArea marea) {
        this.marea = marea;
        return this;
    }

    public Lan clone() {
        return new Lan().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setDescriptionR(this.description).setSubnetIPR(this.subnetIP).
                       setSubnetMaskR(this.subnetMask).setDatacentersR(new HashSet<Datacenter>(this.datacenters)).setMareaR(this.marea).setOsInstancesR(new HashSet<OSInstance>(this.osInstances)).setTypeR(this.type);
    }
}