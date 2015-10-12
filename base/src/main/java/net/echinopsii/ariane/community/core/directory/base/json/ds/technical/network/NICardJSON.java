/**
 * Directory WAT
 * NICard to JSON tools
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
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NICard;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Our own object to JSON tools as :
 * - we can have cycle in object graphs
 * - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class NICardJSON {

    public final static String NICARD_ID = "niCardID";
    public final static String NICARD_VERSION = "niCardVersion";
    public final static String NICARD_NAME = "niCardName";
    public final static String NICARD_MACADDRESS = "niCardMacAddress";
    public final static String NICARD_DUPLEX = "niCardDuplex";
    public final static String NICARD_SPEED = "niCardSpeed";
    public final static String NICARD_MTU = "niCardMtu";
    public final static String NICARD_IPADDRESS_ID = "niCardIPAddressID";
    public final static String NICARD_OSI_ID = "niCardOSInstanceID";


    public final static void niCard2JSON(NICard niCard, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(NICARD_ID, niCard.getId());
        jgenerator.writeNumberField(NICARD_VERSION, niCard.getVersion());
        jgenerator.writeStringField(NICARD_NAME, niCard.getName());
        jgenerator.writeStringField(NICARD_DUPLEX, niCard.getDuplex());
        jgenerator.writeStringField(NICARD_MACADDRESS, niCard.getMacAddress());
        jgenerator.writeNumberField(NICARD_SPEED, niCard.getSpeed());
        jgenerator.writeNumberField(NICARD_MTU, niCard.getMtu());
        jgenerator.writeNumberField(NICARD_OSI_ID, ((niCard.getRosInstance() != null) ? niCard.getRosInstance().getId() : -1));
        jgenerator.writeNumberField(NICARD_IPADDRESS_ID, ((niCard.getRipAddress() != null) ? niCard.getRipAddress().getId() : -1));
        jgenerator.writeEndObject();
    }

    public final static void oneNICard2JSON(NICard niCard, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        NICardJSON.niCard2JSON(niCard, jgenerator);
        jgenerator.close();
    }

    public final static void manyNICards2JSON(HashSet<NICard> niCards, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("nicards");
        Iterator<NICard> iter = niCards.iterator();
        while (iter.hasNext()) {
            NICard current = iter.next();
            NICardJSON.niCard2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlyNICard{
        private long niCardID;
        private String niCardName;
        private String niCardMacAddress;
        private String niCardDuplex;
        private int niCardSpeed;
        private int niCardMtu;
        private long niCardIPAddressID;
        private long niCardOSInstanceID;

        public long getNiCardOSInstanceID() {
            return niCardOSInstanceID;
        }

        public void setNiCardOSInstanceID(long niCardOSInstanceID) {
            this.niCardOSInstanceID = niCardOSInstanceID;
        }

        public long getNiCardIPAddressID() {
            return niCardIPAddressID;
        }

        public void setNiCardIPAddressID(long niCardIPAddressID) {
            this.niCardIPAddressID = niCardIPAddressID;
        }

        public int getNiCardMtu() {
            return niCardMtu;
        }

        public void setNiCardMtu(int niCardMtu) {
            this.niCardMtu = niCardMtu;
        }

        public int getNiCardSpeed() {
            return niCardSpeed;
        }

        public void setNiCardSpeed(int niCardSpeed) {
            this.niCardSpeed = niCardSpeed;
        }

        public String getNiCardDuplex() {
            return niCardDuplex;
        }

        public void setNiCardDuplex(String niCardDuplex) {
            this.niCardDuplex = niCardDuplex;
        }

        public String getNiCardMacAddress() {
            return niCardMacAddress;
        }

        public void setNiCardMacAddress(String niCardMacAddress) {
            this.niCardMacAddress = niCardMacAddress;
        }

        public String getNiCardName() {
            return niCardName;
        }

        public void setNiCardName(String niCardName) {
            this.niCardName = niCardName;
        }

        public long getNiCardID() {
            return niCardID;
        }

        public void setNiCardID(long niCardID) {
            this.niCardID = niCardID;
        }
    }

    public final static JSONFriendlyNICard JSON2NICard(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONFriendlyNICard jsonFriendlyNICard = mapper.readValue(payload, JSONFriendlyNICard.class);
        return jsonFriendlyNICard;
    }
}