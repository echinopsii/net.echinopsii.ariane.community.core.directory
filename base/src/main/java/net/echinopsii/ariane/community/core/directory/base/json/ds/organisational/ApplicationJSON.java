/**
 * Directory WAT
 * Application to JSON tools
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
import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Our own object to JSON tools as :
 *  - we can have cycle in object graphs
 *  - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class ApplicationJSON {

    public final static String APP_ID          = "applicationID";
    public final static String APP_VERSION     = "applicationVersion";
    public final static String APP_NAME        = "applicationName";
    public final static String APP_SHORT_NAME  = "applicationShortName";
    public final static String APP_COLOR_CODE  = "applicationColorCode";
    public final static String APP_DESCRIPTION = "applicationDescription";
    public final static String APP_OSI_ID      = "applicationOSInstancesID";
    public final static String APP_TEAM_ID     = "applicationTeamID";
    public final static String APP_COMPANY_ID  = "applicationCompanyID";

    public final static void application2JSON(Application application, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(APP_ID, application.getId());
        jgenerator.writeNumberField(APP_VERSION, application.getVersion());
        jgenerator.writeStringField(APP_NAME, application.getName());
        jgenerator.writeStringField(APP_SHORT_NAME, application.getShortName());
        jgenerator.writeStringField(APP_COLOR_CODE, application.getColorCode());
        jgenerator.writeStringField(APP_DESCRIPTION, application.getDescription());
        jgenerator.writeArrayFieldStart(APP_OSI_ID);
        for (OSInstance osi : application.getOsInstances())
            jgenerator.writeNumber(osi.getId());
        jgenerator.writeEndArray();
        jgenerator.writeNumberField(APP_TEAM_ID, ((application.getTeam()!=null) ? application.getTeam().getId(): -1));
        jgenerator.writeNumberField(APP_COMPANY_ID, ((application.getCompany()!=null) ? application.getCompany().getId() : -1));
        jgenerator.writeEndObject();
    }

    public final static void oneApplication2JSON(Application application, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        ApplicationJSON.application2JSON(application, jgenerator);
        jgenerator.close();
    }

    public final static void manyApplications2JSON(HashSet<Application> applications, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("applications");
        Iterator<Application> iterC = applications.iterator();
        while (iterC.hasNext()) {
            Application current = iterC.next();
            ApplicationJSON.application2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}