/**
 * Directory WAT
 * Subnet to JSON tools
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
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;

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
public class SubnetJSON {

    public final static String SUBNET_ID = "subnetID";
    public final static String SUBNET_VERSION = "subnetVersion";
    public final static String SUBNET_NAME = "subnetName";
    public final static String SUBNET_DESCRIPTION = "subnetDescription";
    public final static String SUBNET_IP = "subnetIP";
    public final static String SUBNET_MASK = "subnetMask";
    public final static String SUBNET_TYPE = "subnetType";
    public final static String SUBNET_OSI_ID = "subnetOSInstancesID";
    public final static String SUBNET_LOCATIONS_ID = "subnetLocationsID";
    public final static String SUBNET_MAREA_ID = "subnetRoutingAreaID";
    public final static String SUBNET_IPADDRESSES_ID = "subnetIPAddressesID";

    public final static void subnet2JSON(Subnet subnet, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(SUBNET_ID, subnet.getId());
        jgenerator.writeNumberField(SUBNET_VERSION, subnet.getVersion());
        jgenerator.writeStringField(SUBNET_NAME, subnet.getName());
        jgenerator.writeStringField(SUBNET_DESCRIPTION, subnet.getDescription());
        jgenerator.writeStringField(SUBNET_IP, subnet.getSubnetIP());
        jgenerator.writeStringField(SUBNET_MASK, subnet.getSubnetMask());
        jgenerator.writeStringField(SUBNET_TYPE, subnet.getRarea().getType());

        jgenerator.writeArrayFieldStart(SUBNET_OSI_ID);
        for (OSInstance osi : subnet.getOsInstances())
            jgenerator.writeNumber(osi.getId());
        jgenerator.writeEndArray();

        jgenerator.writeArrayFieldStart(SUBNET_LOCATIONS_ID);
        for (Location dc : subnet.getLocations())
            jgenerator.writeNumber(dc.getId());
        jgenerator.writeEndArray();

        jgenerator.writeNumberField(SUBNET_MAREA_ID, ((subnet.getRarea() != null) ? subnet.getRarea().getId() : -1));

        jgenerator.writeArrayFieldStart(SUBNET_IPADDRESSES_ID);
        for (IPAddress ipAddress : subnet.getIpAddresses())
            jgenerator.writeNumber(ipAddress.getId());
        jgenerator.writeEndArray();

        jgenerator.writeEndObject();
    }

    public final static void oneSubnet2JSON(Subnet subnet, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        SubnetJSON.subnet2JSON(subnet, jgenerator);
        jgenerator.close();
    }

    public final static void manySubnets2JSON(HashSet<Subnet> subnets, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("subnets");
        Iterator<Subnet> iter = subnets.iterator();
        while (iter.hasNext()) {
            Subnet current = iter.next();
            SubnetJSON.subnet2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlySubnet {
        private long subnetID;
        private String subnetName;
        private String subnetDescription;
        private String subnetIP;
        private String subnetType;
        private String subnetMask;
        private long subnetRoutingAreaID;
        private List<Long> subnetOSInstancesID;
        private List<Long> subnetLocationsID;
        private List<Long> subnetIPAddressesID;

        public List<Long> getSubnetLocationsID() {
            return subnetLocationsID;
        }

        public void setSubnetLocationsID(List<Long> subnetLocationsID) {
            this.subnetLocationsID = subnetLocationsID;
        }

        public List<Long> getSubnetOSInstancesID() {
            return subnetOSInstancesID;
        }

        public void setSubnetOSInstancesID(List<Long> subnetOSInstancesID) {
            this.subnetOSInstancesID = subnetOSInstancesID;
        }

        public long getSubnetRoutingAreaID() {
            return subnetRoutingAreaID;
        }

        public void setSubnetRoutingAreaID(long subnetRoutingAreaID) {
            this.subnetRoutingAreaID = subnetRoutingAreaID;
        }

        public String getSubnetMask() {
            return subnetMask;
        }

        public void setSubnetMask(String subnetMask) {
            this.subnetMask = subnetMask;
        }

        public String getSubnetType() {
            return subnetType;
        }

        public void setSubnetType(String subnetType) {
            this.subnetType = subnetType;
        }

        public String getSubnetIP() {
            return subnetIP;
        }

        public void setSubnetIP(String subnetIP) {
            this.subnetIP = subnetIP;
        }

        public String getSubnetDescription() {
            return subnetDescription;
        }

        public void setSubnetDescription(String subnetDescription) {
            this.subnetDescription = subnetDescription;
        }

        public String getSubnetName() {
            return subnetName;
        }

        public void setSubnetName(String subnetName) {
            this.subnetName = subnetName;
        }

        public long getSubnetID() {
            return subnetID;
        }

        public void setSubnetID(long subnetID) {
            this.subnetID = subnetID;
        }

        public List<Long> getSubnetIPAddressesID() {
            return subnetIPAddressesID;
        }

        public void setSubnetIPAddressesID(List<Long> subnetIPAddressesID) {
            this.subnetIPAddressesID = subnetIPAddressesID;
        }
    }

    public final static JSONFriendlySubnet JSON2Subnet(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONFriendlySubnet jsonFriendlySubnet = mapper.readValue(payload, JSONFriendlySubnet.class);
        return jsonFriendlySubnet;
    }
}