/**
 * Directory WAT
 * NIC to JSON tools
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

package net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NIC;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Our own object to JSON tools as :
 * - we can have cycle in object graphs
 * - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class NICJSON {

    public final static String NIC_ID = "nicID";
    public final static String NIC_NAME = "nicName";
    public final static String NIC_MACADDRESS = "nicMacAddress";
    public final static String NIC_DUPLEX = "nicDuplex";
    public final static String NIC_SPEED = "nicSpeed";
    public final static String NIC_MTU = "nicMtu";
    public final static String NIC_IPADDRESS_ID = "nicIPAddressID";
    public final static String NIC_OSI_ID = "nicOSInstanceID";


    public final static void nic2JSON(NIC nic, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(NIC_ID, nic.getId());
        jgenerator.writeStringField(NIC_NAME, nic.getName());
        jgenerator.writeStringField(NIC_DUPLEX, nic.getDuplex());
        jgenerator.writeStringField(NIC_MACADDRESS, nic.getMacAddress());
        jgenerator.writeNumberField(NIC_SPEED, nic.getSpeed());
        jgenerator.writeNumberField(NIC_MTU, nic.getMtu());
        jgenerator.writeNumberField(NIC_OSI_ID, ((nic.getOsInstance() != null) ? nic.getOsInstance().getId() : -1));
        jgenerator.writeNumberField(NIC_IPADDRESS_ID, ((nic.getIpAddress() != null) ? nic.getIpAddress().getId() : -1));
        jgenerator.writeEndObject();
    }

    public final static void oneNIC2JSON(NIC nic, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        NICJSON.nic2JSON(nic, jgenerator);
        jgenerator.close();
    }

    public final static void manyNICs2JSON(HashSet<NIC> nics, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("nics");
        Iterator<NIC> iter = nics.iterator();
        while (iter.hasNext()) {
            NIC current = iter.next();
            NICJSON.nic2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlyNIC {
        private long nicID;
        private String nicName;
        private String nicMacAddress;
        private String nicDuplex;
        private int nicSpeed;
        private int nicMtu;
        private long nicIPAddressID;
        private long nicOSInstanceID;

        public long getNicOSInstanceID() {
            return nicOSInstanceID;
        }

        public void setNicOSInstanceID(long nicOSInstanceID) {
            this.nicOSInstanceID = nicOSInstanceID;
        }

        public long getNicIPAddressID() {
            return nicIPAddressID;
        }

        public void setNicIPAddressID(long nicIPAddressID) {
            this.nicIPAddressID = nicIPAddressID;
        }

        public int getNicMtu() {
            return nicMtu;
        }

        public void setNicMtu(int nicMtu) {
            this.nicMtu = nicMtu;
        }

        public int getNicSpeed() {
            return nicSpeed;
        }

        public void setNicSpeed(int nicSpeed) {
            this.nicSpeed = nicSpeed;
        }

        public String getNicDuplex() {
            return nicDuplex;
        }

        public void setNicDuplex(String nicDuplex) {
            this.nicDuplex = nicDuplex;
        }

        public String getNicMacAddress() {
            return nicMacAddress;
        }

        public void setNicMacAddress(String nicMacAddress) {
            this.nicMacAddress = nicMacAddress;
        }

        public String getNicName() {
            return nicName;
        }

        public void setNicName(String nicName) {
            this.nicName = nicName;
        }

        public long getNicID() {
            return nicID;
        }

        public void setNicID(long nicID) {
            this.nicID = nicID;
        }
    }

    public final static JSONFriendlyNIC JSON2NIC(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper.readValue(payload, JSONFriendlyNIC.class);
    }
}