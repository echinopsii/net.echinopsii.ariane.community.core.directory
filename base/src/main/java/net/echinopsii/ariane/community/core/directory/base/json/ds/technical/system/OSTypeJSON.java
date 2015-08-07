/**
 * Directory WAT
 * OS Type to JSON tools
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
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSType;

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
public class OSTypeJSON {

    public final static String OST_ID = "osTypeID";
    public final static String OST_VERSION = "osTypeVersion";
    public final static String OST_NAME = "osTypeName";
    public final static String OST_ARCHITECTURE = "osTypeArchitecture";
    public final static String OST_CMP_ID = "osTypeCompanyID";
    public final static String OST_OSI_ID = "osTypeOSInstancesID";


    public final static void osType2JSON(OSType osType, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(OST_ID, osType.getId());
        jgenerator.writeNumberField(OST_VERSION, osType.getVersion());
        jgenerator.writeStringField(OST_NAME, osType.getName());
        jgenerator.writeStringField(OST_ARCHITECTURE, osType.getArchitecture());
        jgenerator.writeNumberField(OST_CMP_ID, ((osType.getCompany() != null) ? osType.getCompany().getId() : -1));
        jgenerator.writeArrayFieldStart(OST_OSI_ID);
        for (OSInstance osi : osType.getOsInstances())
            jgenerator.writeNumber(osi.getId());
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
    }

    public final static void oneOSType2JSON(OSType osType, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        OSTypeJSON.osType2JSON(osType, jgenerator);
        jgenerator.close();
    }

    public final static void manyOSTypes2JSON(HashSet<OSType> osTypes, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("osTypes");
        Iterator<OSType> iter = osTypes.iterator();
        while (iter.hasNext()) {
            OSType current = iter.next();
            OSTypeJSON.osType2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlyOSType {
        private long osTypeID;
        private long osTypeVersion;
        private String osTypeName;
        private String osTypeArchitecture;
        private long osTypeCompanyID;
        private List<Long> osTypeOSInstancesID;

        public List<Long> getOsTypeOSInstancesID() {
            return osTypeOSInstancesID;
        }

        public void setOsTypeOSInstancesID(List<Long> osTypeOSInstancesID) {
            this.osTypeOSInstancesID = osTypeOSInstancesID;
        }

        public long getOsTypeCompanyID() {
            return osTypeCompanyID;
        }

        public void setOsTypeCompanyID(long osTypeCompanyID) {
            this.osTypeCompanyID = osTypeCompanyID;
        }

        public String getOsTypeArchitecture() {
            return osTypeArchitecture;
        }

        public void setOsTypeArchitecture(String osTypeArchitecture) {
            this.osTypeArchitecture = osTypeArchitecture;
        }

        public String getOsTypeName() {
            return osTypeName;
        }

        public void setOsTypeName(String osTypeName) {
            this.osTypeName = osTypeName;
        }

        public long getOsTypeVersion() {
            return osTypeVersion;
        }

        public void setOsTypeVersion(long osTypeVersion) {
            this.osTypeVersion = osTypeVersion;
        }

        public long getOsTypeID() {
            return osTypeID;
        }

        public void setOsTypeID(long osTypeID) {
            this.osTypeID = osTypeID;
        }
    }

    public final static JSONFriendlyOSType JSON2OSType(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONFriendlyOSType jsonFriendlyOSType = mapper.readValue(payload, JSONFriendlyOSType.class);
        return jsonFriendlyOSType;
    }
}