/**
 * Directory base
 * Model technical/network/location
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.*;

@Entity
@XmlRootElement
@Table(name="location", uniqueConstraints = @UniqueConstraint(columnNames = {"Name"}))
public class Location implements Serializable
{

    private static final Logger log = LoggerFactory.getLogger(Location.class);

    @Transient
    public static String LOCATION_DATACENTER_TYPE = "DATACENTER";
    @Transient
    public static String LOCATION_OFFICE_TYPE = "OFFICE";

    public static boolean isValidType(String type) {
        return (type!=null && (type.equals(LOCATION_DATACENTER_TYPE) || type.equals(LOCATION_OFFICE_TYPE) ));
    }

    public static List<String> getTypeList() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(LOCATION_DATACENTER_TYPE);
        ret.add(LOCATION_OFFICE_TYPE);
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

    @Column(name="Name",unique=true,nullable=false)
    @NotNull
    private String name;

    @Column(nullable=false)
    @NotNull
    private String address;

    @Column(nullable=false)
    @NotNull
    private String town;

    @Column(nullable=false)
    @NotNull
    private Long   zipCode;

    @Column(nullable=false)
    @NotNull
    private String country;

    @Column(nullable=false)
    @NotNull
    private double gpsLatitude;

    @Column(nullable=false)
    @NotNull
    private double gpsLongitude;

    @Column
    private String description;

    @Column
    private String type;

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<Subnet> subnets = new HashSet<Subnet>();

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private Set<RoutingArea> routingAreas = new HashSet<RoutingArea>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Location setTypeR(String type){
        this.type = type;
        return this;
    }
    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Location setIdR(final Long id) {
        this.id = id;
        return this;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public Location setVersionR(final int version) {
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
            return id.equals(((Location) that).id);
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

    public Location setNameR(final String name) {
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

    public Location setAddressR(final String address) {
        this.address = address;
        return this;
    }

    public Long getZipCode() {
        return zipCode;
    }

    public void setZipCode(Long zipCode) {
        this.zipCode = zipCode;
    }

    public Location setZipCodeR(Long zipCode) {
        this.zipCode = zipCode;
        return this;
    }

    public String getTown() {
        return this.town;
    }

    public void setTown(final String town) {
        this.town = town;
    }

    public Location setTownR(final String town) {
        this.town = town;
        return this;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public Location setCountryR(final String country) {
        this.country = country;
        return this;
    }

    public double getGpsLatitude() {
        return this.gpsLatitude;
    }

    public void setGpsLatitude(final double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public Location setGpsLatitudeR(final double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
        return this;
    }

    public double getGpsLongitude() {
        return this.gpsLongitude;
    }

    public void setGpsLongitude(final double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public Location setGpsLongitudeR(final double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
        return this;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Location setDescriptionR(final String description) {
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

    public Location setSubnetsR(final Set<Subnet> subnets) {
        this.subnets = subnets;
        return this;
    }

    public Set<RoutingArea> getRoutingAreas() {
        return this.routingAreas;
    }

    public void setRoutingAreas(final Set<RoutingArea> routingAreas) {
        this.routingAreas = routingAreas;
    }

    public Location setMulticastAreasR(final Set<RoutingArea> routingAreas) {
        this.routingAreas = routingAreas;
        return this;
    }

    @Override
    public Location clone() throws CloneNotSupportedException {
        return new Location().setIdR(this.id).setVersionR(this.version).setNameR(this.name).setAddressR(this.address).setZipCodeR(this.zipCode).setTownR(this.town).
                                setCountryR(this.country).setDescriptionR(this.description).setGpsLatitudeR(this.gpsLatitude).setGpsLongitudeR(this.gpsLongitude).
                                setSubnetsR(new HashSet<Subnet>(this.subnets)).setMulticastAreasR(new HashSet<RoutingArea>(this.routingAreas));
    }

    public  final static String DC_MAPPING_PROPERTIES = "Location";
    private final static String DC_NAME_MAPPING_FIELD = "pname";
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