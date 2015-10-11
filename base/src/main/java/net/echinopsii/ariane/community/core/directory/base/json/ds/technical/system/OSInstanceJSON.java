/**
 * Directory WAT
 * OS Instance to JSON tools
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

package net.echinopsii.ariane.community.core.directory.base.json.ds.technical.system;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NICard;
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
public class OSInstanceJSON {

    public final static String OSI_ID               = "osInstanceID";
    public final static String OSI_VERSION          = "osInstanceVersion";
    public final static String OSI_NAME             = "osInstanceName";
    public final static String OSI_ADMIN_GATE_URI   = "osInstanceAdminGateURI";
    public final static String OSI_DESCRIPTION      = "osInstanceDescription";
    public final static String OSI_SUBNETS_ID       = "osInstanceSubnetsID";
    public final static String OSI_EMBEDDING_OSI_ID = "osInstanceEmbeddingOSInstanceID";
    public final static String OSI_EMBEDDED_OSI_ID  = "osInstanceEmbeddedOSInstancesID";
    public final static String OSI_OST_ID           = "osInstanceOSTypeID";
    public final static String OSI_APPS_ID          = "osInstanceApplicationsID";
    public final static String OSI_TEAMS_ID         = "osInstanceTeamsID";
    public final static String OSI_ENVS_ID          = "osInstanceEnvironmentsID";
    public final static String OSI_IPADDRESSES_ID   = "osInstanceIPAddressesID";
    public final static String OSI_NICS_ID          = "osInstanceNICardsID";

    public final static void osInstance2JSON(OSInstance osInstance, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(OSI_ID, osInstance.getId());
        jgenerator.writeNumberField(OSI_VERSION, osInstance.getVersion());
        jgenerator.writeStringField(OSI_NAME, osInstance.getName());
        jgenerator.writeStringField(OSI_ADMIN_GATE_URI, osInstance.getAdminGateURI());
        jgenerator.writeStringField(OSI_DESCRIPTION, osInstance.getDescription());
        jgenerator.writeArrayFieldStart(OSI_SUBNETS_ID);
        if (osInstance.getNetworkSubnets()!=null)
            for (Subnet subnet : osInstance.getNetworkSubnets())
                jgenerator.writeNumber(subnet.getId());
        jgenerator.writeEndArray();
        jgenerator.writeNumberField(OSI_EMBEDDING_OSI_ID, ((osInstance.getEmbeddingOSInstance()!=null) ? osInstance.getEmbeddingOSInstance().getId():-1));
        jgenerator.writeArrayFieldStart(OSI_EMBEDDED_OSI_ID);
        for (OSInstance eosi : osInstance.getEmbeddedOSInstances())
            jgenerator.writeNumber(eosi.getId());
        jgenerator.writeEndArray();
        jgenerator.writeNumberField(OSI_OST_ID, ((osInstance.getOsType() != null) ? osInstance.getOsType().getId() : -1));
        jgenerator.writeArrayFieldStart(OSI_APPS_ID);
        for (Application app : osInstance.getApplications())
            jgenerator.writeNumber(app.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(OSI_TEAMS_ID);
        for (Team team : osInstance.getTeams())
            jgenerator.writeNumber(team.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(OSI_ENVS_ID);
        for (Environment env : osInstance.getEnvironments())
            jgenerator.writeNumber(env.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(OSI_IPADDRESSES_ID);
        for (IPAddress ipAddress : osInstance.getIpAddresses())
            jgenerator.writeNumber(ipAddress.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(OSI_NICS_ID);
        for (NICard niCard : osInstance.getNiCards())
            jgenerator.writeNumber(niCard.getId());
        jgenerator.writeEndArray();

        jgenerator.writeEndObject();
    }

    public final static void oneOSInstance2JSON(OSInstance osInstance, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        OSInstanceJSON.osInstance2JSON(osInstance, jgenerator);
        jgenerator.close();
    }

    public final static void manyOSInstances2JSON(HashSet<OSInstance> osInstances, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("osInstances");
        Iterator<OSInstance> iter = osInstances.iterator();
        while (iter.hasNext()) {
            OSInstance current = iter.next();
            OSInstanceJSON.osInstance2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlyOSInstance {
        private long osInstanceID;
        private String osInstanceName;
        private String osInstanceAdminGateURI;
        private String osInstanceDescription;
        private long osInstanceEmbeddingOSInstanceID;
        private long osInstanceOSTypeID;
        private List<Long> osInstanceSubnetsID;
        private List<Long> osInstanceEmbeddedOSInstancesID;
        private List<Long> osInstanceApplicationsID;
        private List<Long> osInstanceTeamsID;
        private List<Long> osInstanceEnvironmentsID;
        private List<Long> osInstanceIPAddressesID;
        private List<Long> osInstanceNICardsID;

        public List<Long> getOsInstanceNICardsID() {
            return osInstanceNICardsID;
        }

        public void setOsInstanceNICardsID(List<Long> osInstanceNICardsID) {
            this.osInstanceNICardsID = osInstanceNICardsID;
        }

        public List<Long> getOsInstanceIPAddressesID() {
            return osInstanceIPAddressesID;
        }

        public void setOsInstanceIPAddressesID(List<Long> osInstanceIPAddressesID) {
            this.osInstanceIPAddressesID = osInstanceIPAddressesID;
        }

        public List<Long> getOsInstanceEnvironmentsID() {
            return osInstanceEnvironmentsID;
        }

        public void setOsInstanceEnvironmentsID(List<Long> osInstanceEnvironmentsID) {
            this.osInstanceEnvironmentsID = osInstanceEnvironmentsID;
        }

        public List<Long> getOsInstanceTeamsID() {
            return osInstanceTeamsID;
        }

        public void setOsInstanceTeamsID(List<Long> osInstanceTeamsID) {
            this.osInstanceTeamsID = osInstanceTeamsID;
        }

        public List<Long> getOsInstanceApplicationsID() {
            return osInstanceApplicationsID;
        }

        public void setOsInstanceApplicationsID(List<Long> osInstanceApplicationsID) {
            this.osInstanceApplicationsID = osInstanceApplicationsID;
        }

        public List<Long> getOsInstanceEmbeddedOSInstancesID() {
            return osInstanceEmbeddedOSInstancesID;
        }

        public void setOsInstanceEmbeddedOSInstancesID(List<Long> osInstanceEmbeddedOSInstancesID) {
            this.osInstanceEmbeddedOSInstancesID = osInstanceEmbeddedOSInstancesID;
        }

        public List<Long> getOsInstanceSubnetsID() {
            return osInstanceSubnetsID;
        }

        public void setOsInstanceSubnetsID(List<Long> osInstanceSubnetsID) {
            this.osInstanceSubnetsID = osInstanceSubnetsID;
        }

        public long getOsInstanceOSTypeID() {
            return osInstanceOSTypeID;
        }

        public void setOsInstanceOSTypeID(long osInstanceOSTypeID) {
            this.osInstanceOSTypeID = osInstanceOSTypeID;
        }

        public long getOsInstanceEmbeddingOSInstanceID() {
            return osInstanceEmbeddingOSInstanceID;
        }

        public void setOsInstanceEmbeddingOSInstanceID(long osInstanceEmbeddingOSInstanceID) {
            this.osInstanceEmbeddingOSInstanceID = osInstanceEmbeddingOSInstanceID;
        }

        public String getOsInstanceDescription() {
            return osInstanceDescription;
        }

        public void setOsInstanceDescription(String osInstanceDescription) {
            this.osInstanceDescription = osInstanceDescription;
        }

        public String getOsInstanceAdminGateURI() {
            return osInstanceAdminGateURI;
        }

        public void setOsInstanceAdminGateURI(String osInstanceAdminGateURI) {
            this.osInstanceAdminGateURI = osInstanceAdminGateURI;
        }

        public String getOsInstanceName() {
            return osInstanceName;
        }

        public void setOsInstanceName(String osInstanceName) {
            this.osInstanceName = osInstanceName;
        }

        public long getOsInstanceID() {
            return osInstanceID;
        }

        public void setOsInstanceID(long osInstanceID) {
            this.osInstanceID = osInstanceID;
        }
    }

    public final static JSONFriendlyOSInstance JSON2OSInstance(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONFriendlyOSInstance jsonFriendlyOSInstance = mapper.readValue(payload, JSONFriendlyOSInstance.class);
        return jsonFriendlyOSInstance;
    }
}