/**
 * Directory base
 * Model technical/network/multicastarea
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

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@XmlRootElement
@Table(name="routingArea",uniqueConstraints = @UniqueConstraint(columnNames = {"rareaName"}))
public class RoutingArea implements Serializable
{
    @Transient
    public static String ROUTING_AREA_DMZ_TYPE = "DMZ";
    @Transient
    public static String ROUTING_AREA_LAN_TYPE = "LAN";
    @Transient
    public static String ROUTING_AREA_MAN_TYPE = "MAN";
    @Transient
    public static String ROUTING_AREA_WAN_TYPE = "WAN";
    @Transient
    public static String ROUTING_AREA_VIRT_TYPE = "VIRT";
    @Transient
    public static String ROUTING_AREA_VPN_TYPE = "VPN";

    public static boolean isValidType(String type) {
        return (type!=null && (type.equals(ROUTING_AREA_LAN_TYPE) || type.equals(ROUTING_AREA_MAN_TYPE) ||
                type.equals(ROUTING_AREA_WAN_TYPE) || type.equals(ROUTING_AREA_VIRT_TYPE)) ||
                type.equals(ROUTING_AREA_VPN_TYPE)
        );
    }

    public static List<String> getTypeList() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(ROUTING_AREA_LAN_TYPE);
        ret.add(ROUTING_AREA_MAN_TYPE);
        ret.add(ROUTING_AREA_WAN_TYPE);
        ret.add(ROUTING_AREA_VIRT_TYPE);
        ret.add(ROUTING_AREA_VPN_TYPE);
        return ret;
    }

    @Transient
    public static String ROUTING_AREA_MULTICAST_NONE = "NONE";
    @Transient
    public static String ROUTING_AREA_MULTICAST_FILTERED = "FILTERED";
    @Transient
    public static String ROUTING_AREA_MULTICAST_NOLIMIT = "NOLIMIT";

    public static boolean isValidMulticastFlag(String multicastFlag) {
        return (multicastFlag!=null && (multicastFlag.equals(ROUTING_AREA_MULTICAST_FILTERED) || multicastFlag.equals(ROUTING_AREA_MULTICAST_NOLIMIT) || multicastFlag.equals(ROUTING_AREA_MULTICAST_NONE)));
    }

    public static List<String> getMulticastFlagList() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(ROUTING_AREA_MULTICAST_NONE);
        ret.add(ROUTING_AREA_MULTICAST_FILTERED);
        ret.add(ROUTING_AREA_MULTICAST_NOLIMIT);
        return ret;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    @NotNull
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="rareaName",unique=true,nullable=false)
    @NotNull
    private String name;

    @Column
    private String description;

    @Column(nullable=false)
    @NotNull
    private String multicast;

    @Column(nullable=false)
    @NotNull
    private String type;

    @OneToMany(mappedBy = "rarea", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Subnet> subnets = new HashSet<Subnet>();

    @ManyToMany(mappedBy = "routingAreas", fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Location> locations = new HashSet<Location>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public RoutingArea setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public RoutingArea setVersionR(final int version) {
        this.version = version ;
        return this;
    }

    @Override
    public boolean equals(Object that)
    {
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
            return id.equals(((RoutingArea) that).id);
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

    public RoutingArea setNameR(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public RoutingArea setDescriptionR(final String description) {
        this.description = description;
        return this;
    }

    public String getMulticast() {
        return multicast;
    }

    public void setMulticast(String multicast) {
        this.multicast = multicast;
    }

    public RoutingArea setMulticastR(String multicast) {
        this.multicast = multicast;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RoutingArea setTypeR(String type) {
        this.type = type;
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

    public Set<Subnet> getSubnets() {
        return this.subnets;
    }

    public void setSubnets(final Set<Subnet> subnets) {
        this.subnets = subnets;
    }

    public RoutingArea setSubnetsR(final Set<Subnet> subnets) {
        this.subnets = subnets;
        return this;
    }

    public Set<Location> getLocations() {
        return this.locations;
    }

    public void setLocations(final Set<Location> locations) {
        this.locations = locations;
    }

    public RoutingArea setDatacentersR(final Set<Location> locations) {
        this.locations = locations;
        return this;
    }

    public RoutingArea clone() {
        return new RoutingArea().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setDescriptionR(this.description).setMulticastR(this.multicast).
                                 setTypeR(this.type).setDatacentersR(new HashSet<Location>(this.locations)).setSubnetsR(new HashSet<Subnet>(this.subnets));
    }
}