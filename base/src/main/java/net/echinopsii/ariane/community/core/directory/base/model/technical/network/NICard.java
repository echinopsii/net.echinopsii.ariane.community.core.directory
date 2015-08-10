/**
 * Directory base
 * Model technical/network/NICard
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

@Entity
@XmlRootElement
@Table(name="niCard", uniqueConstraints = @UniqueConstraint(columnNames = {"macAddress"}))
public class NICard {
    private static final Logger log = LoggerFactory.getLogger(NICard.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;

    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name = "macAddress",unique = true, nullable = false)
    @NotNull
    private String macAddress;

    @Column(name = "name")
    private String name;

    @Column(name = "duplex")
    private String duplex;

    @Column(name = "speed")
    private int speed;

    @Column(name = "mtu")
    private int mtu;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn
    private IPAddress ripAddress;

    public IPAddress getRipAddress() {
        return ripAddress;
    }

    public void setRipAddress(IPAddress ipAddress) {
        this.ripAddress = ipAddress;
    }

    public NICard setIpAddressR(IPAddress ipAddress){
        this.ripAddress = ipAddress;
        return this;
    }

    public int getMtu() {
        return mtu;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public NICard setMtuR(int mtu){
        this.mtu = mtu;
        return this;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public NICard setSpeedR(int speed){
        this.speed = speed;
        return this;
    }

    public String getDuplex() {
        return duplex;
    }

    public void setDuplex(String duplex) {
        this.duplex = duplex;
    }

    public  NICard setDuplexR(String duplex)
    {
        this.duplex = duplex;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NICard setNameR(String name){
        this.name = name;
        return this;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public NICard setMacAddressR(String macAddress){
        this.macAddress = macAddress;
        return this;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NICard setIdR(Long id){
        this.id = id;
        return this;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public NICard setVersionR(int version) {
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
            return id.equals(((NICard) that).id);
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
        if (macAddress != null && !macAddress.trim().isEmpty())
            result += ", MAC Address: " + macAddress;
        return result;
    }

    public NICard clone() {
        return new NICard().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setIpAddressR(this.ripAddress).
                setDuplexR(this.duplex).setMacAddressR(this.macAddress).setMtuR(this.mtu).setSpeedR(this.speed);
    }
}
