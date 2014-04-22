/**
 * Directory base
 * Model technical/network/datacenter
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
@Table(name="datacenter", uniqueConstraints = @UniqueConstraint(columnNames = {"dcName"}))
public class Datacenter implements Serializable
{

    private static final Logger log = LoggerFactory.getLogger(Datacenter.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
    @Version
    @Column(name = "version")
    private int version = 0;

    @Column(name="dcName",unique=true)
    @NotNull
    private String name;

    @Column
    private String address;

    @Column
    private String town;

    @Column
    private Long   zipCode;

    @Column
    private String country;

    @Column
    private double gpsLatitude;

    @Column
    private double gpsLongitude;

    @Column
    private String description;

    @ManyToMany
    private Set<Subnet> subnets = new HashSet<Subnet>();

    @ManyToMany
    private Set<MulticastArea> multicastAreas = new HashSet<MulticastArea>();

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Datacenter setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public Datacenter setVersionR(final int version) {
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
            return id.equals(((Datacenter) that).id);
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

    public Datacenter setNameR(final String name) {
        this.name = name;
        return this;
    }

    public void setName(final String name) {
        log.debug("Set Name with {}", new Object[]{name});
        this.name = name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public Datacenter setAddressR(final String address) {
        this.address = address;
        return this;
    }

    public Long getZipCode() {
        return zipCode;
    }

    public void setZipCode(Long zipCode) {
        this.zipCode = zipCode;
    }

    public Datacenter setZipCodeR(Long zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public String getTown() {
        return this.town;
    }

    public void setTown(final String town) {
        this.town = town;
    }

    public Datacenter setTownR(final String town) {
        this.town = town;
        return this;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public Datacenter setCountryR(final String country) {
        this.country = country;
        return this;
    }

    public double getGpsLatitude() {
        return this.gpsLatitude;
    }

    public void setGpsLatitude(final double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public Datacenter setGpsLatitudeR(final double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
        return this;
    }

    public double getGpsLongitude() {
        return this.gpsLongitude;
    }

    public void setGpsLongitude(final double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public Datacenter setGpsLongitudeR(final double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Datacenter setDescriptionR(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty())
            result += "name: " + name;
        if (address != null && !address.trim().isEmpty())
            result += ", address: " + address;
        if (town != null && !town.trim().isEmpty())
            result += ", town: " + town;
        if (country != null && !country.trim().isEmpty())
            result += ", country: " + country;
        result += ", gpsLatitude: " + gpsLatitude;
        result += ", gpsLongitude: " + gpsLongitude;
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

    public Datacenter setSubnetsR(final Set<Subnet> subnets) {
        this.subnets = subnets;
        return this;
    }

    public Set<MulticastArea> getMulticastAreas() {
        return this.multicastAreas;
    }

    public void setMulticastAreas(final Set<MulticastArea> multicastAreas) {
        this.multicastAreas = multicastAreas;
    }

    public Datacenter setMulticastAreasR(final Set<MulticastArea> multicastAreas) {
        this.multicastAreas = multicastAreas;
        return this;
    }

    @Override
    public Datacenter clone() throws CloneNotSupportedException {
        return new Datacenter().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setAddressR(this.address).setZipCodeR(this.zipCode).setTownR(this.town).
                                setCountryR(this.country).setDescriptionR(this.description).setGpsLatitudeR(this.gpsLatitude).setGpsLongitudeR(this.gpsLongitude).
                                setSubnetsR(new HashSet<Subnet>(this.subnets)).setMulticastAreasR(new HashSet<MulticastArea>(this.multicastAreas));
    }

    public  final static String DC_MAPPING_PROPERTIES = "Datacenter";
    private final static String DC_NAME_MAPPING_FIELD = "dc";
    private final static String DC_ADDR_MAPPING_FIELD = "address";
    private final static String DC_TOWN_MAPPING_FIELD = "town";
    private final static String DC_CNTY_MAPPING_FIELD = "country";
    private final static String DC_GPSA_MAPPING_FIELD = "gpsLat";
    private final static String DC_GPSN_MAPPING_FIELD = "gpsLng";

    public HashMap<String,Object> toMappingProperties() {
        HashMap<String,Object> ret = new HashMap<String,Object>();
        ret.put(DC_NAME_MAPPING_FIELD,name);
        ret.put(DC_ADDR_MAPPING_FIELD,address);
        ret.put(DC_TOWN_MAPPING_FIELD,town);
        ret.put(DC_CNTY_MAPPING_FIELD,country);
        ret.put(DC_GPSA_MAPPING_FIELD,gpsLatitude);
        ret.put(DC_GPSN_MAPPING_FIELD,gpsLongitude);
        return ret;
    }
}