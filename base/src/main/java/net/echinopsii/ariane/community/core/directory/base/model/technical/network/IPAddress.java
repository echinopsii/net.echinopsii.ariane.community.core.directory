/**
 * Directory base
 * Model technical/network/subnet
 * Copyright (C) 2015 Echinopsii
 * Author : Sagar Ghuge
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.HashMap;


@Entity
@XmlRootElement
@Table(name="ipaddress", uniqueConstraints = @UniqueConstraint(columnNames = {"id"}))
public class IPAddress implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(IPAddress.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;

    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name = "ipAddress", nullable = false)
    @NotNull
    private String ipAddress;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @NotNull
    private Subnet networkSubnet;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public IPAddress setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public IPAddress setVersionR(final int version) {
        this.version = version;
        return this;
    }

    public String getIpAddress() {
        return this.ipAddress;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public IPAddress setIpAddressR(final String ipAddress) {
        this.ipAddress = ipAddress;
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
            return id.equals(((IPAddress) that).id);
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

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (ipAddress != null && !ipAddress.trim().isEmpty())
            result += ", IPAddress: " + ipAddress;
        return result;
    }

    public Subnet getNetworkSubnet() {
        return this.networkSubnet;
    }

    public void setNetworkSubnet(final Subnet networkSubnet) {
        this.networkSubnet = networkSubnet;
    }

    public IPAddress setNetworkSubnetR(final Subnet networkSubnet) {
        this.networkSubnet = networkSubnet;
        return this;
    }

    public IPAddress clone() {
        return new IPAddress().setIdR(this.id).setVersionR(this.version).setIpAddressR(this.ipAddress).setNetworkSubnetR(this.networkSubnet);
    }

    public final static String SUBNET_SUBNET_MAPPING_FIELD = "networkSubnet";

    public HashMap<String, Object> toMappingProperties() {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put(SUBNET_SUBNET_MAPPING_FIELD, networkSubnet.getName());
        return ret;
    }
}