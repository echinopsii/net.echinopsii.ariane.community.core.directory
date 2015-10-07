/**
 * Directory WAT
 * Location to JSON tools
 * Copyright (C) 06/05/14 echinopsii
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

package net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Location;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Our own object to JSON tools as :
 *  - we can have cycle in object graphs
 *  - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class LocationJSON {

    public final static String LOC_ID          = "locationID";
    public final static String LOC_VERSION     = "locationVersion";
    public final static String LOC_NAME        = "locationName";
    public final static String LOC_ADDRESS     = "locationAddress";
    public final static String LOC_TOWN        = "locationTown";
    public final static String LOC_ZIPCODE     = "locationZipCode";
    public final static String LOC_COUNTRY     = "locationCountry";
    public final static String LOC_GPSLAT      = "locationGPSLat";
    public final static String LOC_GPSLNG      = "locationGPSLng";
    public final static String LOC_DESCRIPTION = "locationDescription";
    public final static String LOC_SUBNETS_ID  = "locationSubnetsID";
    public final static String LOC_MAREAS_ID   = "locationRoutingAreasID";

    public final static void location2JSON(Location location, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(LOC_ID, location.getId());
        jgenerator.writeNumberField(LOC_VERSION, location.getVersion());
        jgenerator.writeStringField(LOC_NAME, location.getName());
        jgenerator.writeStringField(LOC_ADDRESS, location.getAddress());
        jgenerator.writeStringField(LOC_TOWN, location.getTown());
        jgenerator.writeNumberField(LOC_ZIPCODE, location.getZipCode());
        jgenerator.writeStringField(LOC_COUNTRY, location.getCountry());
        jgenerator.writeNumberField(LOC_GPSLAT, location.getGpsLatitude());
        jgenerator.writeNumberField(LOC_GPSLNG, location.getGpsLongitude());
        jgenerator.writeStringField(LOC_DESCRIPTION, location.getDescription());
        jgenerator.writeArrayFieldStart(LOC_SUBNETS_ID);
        for (Subnet subnet : location.getSubnets())
            jgenerator.writeNumber(subnet.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(LOC_MAREAS_ID);
        for (RoutingArea marea : location.getRoutingAreas())
            jgenerator.writeNumber(marea.getId());
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
    }

    public final static void oneLocation2JSON(Location location, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        LocationJSON.location2JSON(location, jgenerator);
        jgenerator.close();
    }

    public final static void manyLocations2JSON(HashSet<Location> locations, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("locations");
        Iterator<Location> iter = locations.iterator();
        while (iter.hasNext()) {
            Location current = iter.next();
            LocationJSON.location2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlyLocation {
        private long locationID;
        private long locationVersion;
        private String locationName;
        private String locationAddress;
        private long locationZipCode;
        private String locationTown;
        private String locationCountry;
        private double locationGPSLat;
        private double locationGPSLng;
        private String locationDescription;
        private List<Long> locationSubnetsID;
        private List<Long> locationRoutingAreasID;

        public String getLocationTown() {
            return locationTown;
        }

        public void setLocationTown(String locationTown) {
            this.locationTown = locationTown;
        }

        public List<Long> getLocationRoutingAreasID() {
            return locationRoutingAreasID;
        }

        public void setLocationRoutingAreasID(List<Long> locationRoutingAreasID) {
            this.locationRoutingAreasID = locationRoutingAreasID;
        }

        public List<Long> getLocationSubnetsID() {
            return locationSubnetsID;
        }

        public void setLocationSubnetsID(List<Long> locationSubnetsID) {
            this.locationSubnetsID = locationSubnetsID;
        }

        public String getLocationDescription() {
            return locationDescription;
        }

        public void setLocationDescription(String locationDescription) {
            this.locationDescription = locationDescription;
        }

        public double getLocationGPSLng() {
            return locationGPSLng;
        }

        public void setLocationGPSLng(double locationGPSLng) {
            this.locationGPSLng = locationGPSLng;
        }

        public double getLocationGPSLat() {
            return locationGPSLat;
        }

        public void setLocationGPSLat(double locationGPSLat) {
            this.locationGPSLat = locationGPSLat;
        }

        public String getLocationCountry() {
            return locationCountry;
        }

        public void setLocationCountry(String locationCountry) {
            this.locationCountry = locationCountry;
        }

        public long getLocationZipCode() {
            return locationZipCode;
        }

        public void setLocationZipCode(long locationZipCode) {
            this.locationZipCode = locationZipCode;
        }

        public String getLocationAddress() {
            return locationAddress;
        }

        public void setLocationAddress(String locationAddress) {
            this.locationAddress = locationAddress;
        }

        public String getLocationName() {
            return locationName;
        }

        public void setLocationName(String locationName) {
            this.locationName = locationName;
        }

        public long getLocationVersion() {
            return locationVersion;
        }

        public void setLocationVersion(long locationVersion) {
            this.locationVersion = locationVersion;
        }

        public long getLocationID() {
            return locationID;
        }

        public void setLocationID(long locationID) {
            this.locationID = locationID;
        }
    }

    public final static JSONFriendlyLocation JSON2Location(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONFriendlyLocation jsonFriendlyLocation = mapper.readValue(payload, JSONFriendlyLocation.class);
        return jsonFriendlyLocation;
    }
}