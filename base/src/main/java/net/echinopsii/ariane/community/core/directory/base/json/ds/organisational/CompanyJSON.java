/**
 * Directory WAT
 * Company to JSON tools
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

package net.echinopsii.ariane.community.core.directory.base.json.ds.organisational;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
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
public class CompanyJSON {

    public final static String CMP_ID          = "companyID";
    public final static String CMP_VERSION     = "companyVersion";
    public final static String CMP_NAME        = "companyName";
    public final static String CMP_DESCRIPTION = "companyDescription";
    public final static String CMP_APP_ID      = "companyApplicationsID";
    public final static String CMP_OST_ID      = "companyOSTypesID";

    public final static void company2JSON(Company company, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(CMP_ID, company.getId());
        jgenerator.writeNumberField(CMP_VERSION, company.getVersion());
        jgenerator.writeStringField(CMP_NAME, company.getName());
        jgenerator.writeStringField(CMP_DESCRIPTION, company.getDescription());
        jgenerator.writeArrayFieldStart(CMP_APP_ID);
        for (Application app : company.getApplications())
            jgenerator.writeNumber(app.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(CMP_OST_ID);
        for (OSType ost : company.getOsTypes())
            jgenerator.writeNumber(ost.getId());
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
    }

    public final static void oneCompany2JSON(Company company, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        CompanyJSON.company2JSON(company, jgenerator);
        jgenerator.close();
    }

    public final static void manyCompanies2JSON(HashSet<Company> companies, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("companies");
        Iterator<Company> iter = companies.iterator();
        while (iter.hasNext()) {
            Company current = iter.next();
            CompanyJSON.company2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }

    public static class JSONFriendlyCompany {
        private long companyID;
        private long companyVersion;
        private String companyName;
        private String companyDescription;
        private List<Long> companyApplicationsID;
        private List<Long> companyOSTypesID;

        public List<Long> getCompanyOSTypesID() {
            return companyOSTypesID;
        }

        public void setCompanyOSTypesID(List<Long> companyOSTypesID) {
            this.companyOSTypesID = companyOSTypesID;
        }

        public List<Long> getCompanyApplicationsID() {
            return companyApplicationsID;
        }

        public void setCompanyApplicationsID(List<Long> companyApplicationsID) {
            this.companyApplicationsID = companyApplicationsID;
        }

        public String getCompanyDescription() {
            return companyDescription;
        }

        public void setCompanyDescription(String companyDescription) {
            this.companyDescription = companyDescription;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public long getCompanyVersion() {
            return companyVersion;
        }

        public void setCompanyVersion(long companyVersion) {
            this.companyVersion = companyVersion;
        }

        public long getCompanyID() {
            return companyID;
        }

        public void setCompanyID(long companyID) {
            this.companyID = companyID;
        }
    }

    public final static JSONFriendlyCompany JSON2Company(String payload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONFriendlyCompany jsonFriendlyComp = mapper.readValue(payload, JSONFriendlyCompany.class);
        return jsonFriendlyComp;
    }

}
