/**
 * Directory base
 * Model technical/network/IPAddress
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

import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


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

    @Column(name = "fqdn", unique = true)
    private String fqdn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    @NotNull
    private Subnet networkSubnet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private OSInstance osInstances;

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

    public String getFqdn() {
        return fqdn;
    }

    public void setFqdn(final String fqdn) {
        this.fqdn = fqdn;
    }

    public IPAddress setFqdnR(final String fqdn){
        this.fqdn = fqdn;
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
            result += ", IP Address: " + ipAddress;
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

    public OSInstance getOsInstances() {
        return this.osInstances;
    }

    public void setOsInstances(final OSInstance osInstances) {
        this.osInstances = osInstances;
    }

    public IPAddress setOsInstancesR(final OSInstance osInstances) {
        this.osInstances = osInstances;
        return this;
    }

    public IPAddress clone() {
        return new IPAddress().setIdR(this.id).setVersionR(this.version).setFqdnR(this.fqdn).setIpAddressR(this.ipAddress).
                               setNetworkSubnetR(this.networkSubnet).setOsInstancesR(this.osInstances);
    }

    public final static String SUBNET_SUBNET_MAPPING_FIELD = "networkSubnet";

    public HashMap<String, Object> toMappingProperties() {
        HashMap<String, Object> ret = new HashMap<String, Object>();
        ret.put(SUBNET_SUBNET_MAPPING_FIELD, networkSubnet.getName());
        return ret;
    }


    public int convertIPToInt(String IP) throws UnknownHostException {
        Inet4Address inet4Address = (Inet4Address) InetAddress.getByName(IP);
        byte[] inetByte = inet4Address.getAddress();
        int intIP = ((inetByte[0] & 0xFF) << 24) |
                ((inetByte[1] & 0xFF) << 16) |
                ((inetByte[2] & 0xFF) << 8)  |
                ((inetByte[3] & 0xFF) << 0);
        return intIP;
    }

    public Boolean checkIP() throws UnknownHostException{
        int intIP = this.convertIPToInt(this.getIpAddress());
        int intSubnetIP = this.convertIPToInt(this.getNetworkSubnet().getSubnetIP());
        int intSubnetMask = this.convertIPToInt(this.getNetworkSubnet().getSubnetMask());
        Boolean isValidate = false;
        if((intSubnetIP & intSubnetMask) != (intIP & intSubnetMask)) {
            isValidate = true;
        }
        return isValidate;
    }

    /**
     * Check if IpAddress is already bind to subnet
     *
     */

    public Boolean isExist(){
        Boolean exist = false;
        for (IPAddress ipa: this.getNetworkSubnet().getIpAddress()){
            if(ipa.getIpAddress().equals(this.ipAddress)){
                exist = true;
                break;
            }
        }

        return exist;
    }
}