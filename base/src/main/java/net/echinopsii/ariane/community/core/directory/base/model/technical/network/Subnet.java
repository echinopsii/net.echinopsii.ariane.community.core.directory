/**
 * Directory base
 * Model technical/network/subnet
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
package net.echinopsii.ariane.community.core.directory.base.model.technical.network;

import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="subnet", uniqueConstraints = @UniqueConstraint(columnNames = {"subnetName"}))
public class Subnet implements Serializable
{
    private static final Logger log = LoggerFactory.getLogger(Subnet.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="subnetName", unique=true, nullable=false)
    @NotNull
    private String name;

    @Column
    private String description;

    @Column(nullable=false)
    @NotNull
    private String subnetIP;

    @Column(nullable=false)
    @NotNull
    private String subnetMask;

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<OSInstance> osInstances = new HashSet<OSInstance>();

    @ManyToMany(mappedBy = "subnets", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Location> locations = new HashSet<Location>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @NotNull
    private RoutingArea rarea;

    @OneToMany(mappedBy = "networkSubnet", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Set<IPAddress> ipAddresses = new HashSet<IPAddress>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Subnet setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public Subnet setVersionR(final int version) {
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
            return id.equals(((Subnet) that).id);
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

    public Subnet setNameR(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public Subnet setDescriptionR (String description) {
        this.description = description;
        return this;
    }

    public String getSubnetIP() {
        return this.subnetIP;
    }

    public void setSubnetIP(final String subnetIP) {
        this.subnetIP = subnetIP;
    }

    public Subnet setSubnetIPR(final String subnetIP) {
        this.subnetIP = subnetIP;
        return this;
    }

    public String getSubnetMask() {
        return this.subnetMask;
    }

    public void setSubnetMask(final String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public Subnet setSubnetMaskR(final String subnetMask) {
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

    public Subnet setOsInstancesR(final Set<OSInstance> osInstances) {
        this.osInstances = osInstances;
        return this;
    }

    public Set<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(final Set<Location> locations) {
        this.locations = locations;
    }

    public Subnet setLocationsR(final Set<Location> locations) {
        this.locations = locations;
        return this;
    }

    public Set<IPAddress> getIpAddresses() {
        return this.ipAddresses;
    }

    public void setIpAddresses(final Set<IPAddress> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public Subnet setIpAddressR(final Set<IPAddress> ipAddress){
        this.ipAddresses = ipAddress;
        return this;
    }

    public RoutingArea getRarea() {
        return this.rarea;
    }

    public void setRarea(final RoutingArea rarea) {
        this.rarea = rarea;
    }

    public Subnet setRareaR(final RoutingArea rarea) {
        this.rarea = rarea;
        return this;
    }

    public Subnet clone() {
        return new Subnet().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setDescriptionR(this.description).setSubnetIPR(this.subnetIP).
                       setSubnetMaskR(this.subnetMask).setLocationsR(new HashSet<Location>(this.locations)).setRareaR(this.rarea).
                       setOsInstancesR(new HashSet<OSInstance>(this.osInstances)).setIpAddressR(new HashSet<IPAddress>(this.ipAddresses));
    }

    public final static String SUBNET_MAPPING_PROPERTIES = "Network";
    public final static String SUBNET_RARE_MAPPING_FIELD = "raname";
    public final static String SUBNET_MLTC_MAPPING_FIELD = "ramulticast";
    public final static String SUBNET_TYPE_MAPPING_FIELD = "ratype";
    public final static String SUBNET_NAME_MAPPING_FIELD = "sname";
    public final static String SUBNET_IPAD_MAPPING_FIELD = "sip";
    public final static String SUBNET_MASK_MAPPING_FIELD = "smask";

    public HashMap<String,Object> toMappingProperties() {
        HashMap<String,Object> ret = new HashMap<String,Object>();
        ret.put(SUBNET_RARE_MAPPING_FIELD, rarea.getName());
        ret.put(SUBNET_MLTC_MAPPING_FIELD, rarea.getMulticast());
        ret.put(SUBNET_TYPE_MAPPING_FIELD, rarea.getType());
        ret.put(SUBNET_NAME_MAPPING_FIELD, name);
        ret.put(SUBNET_IPAD_MAPPING_FIELD, subnetIP);
        ret.put(SUBNET_MASK_MAPPING_FIELD, subnetMask);
        return ret;
    }
}