/**
 * Directory WAT
 * Environment to JSON tools
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

package net.echinopsii.ariane.core.directory.wat.json.ds.organisational;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import net.echinopsii.ariane.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.core.directory.wat.DirectoryBootstrap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Our own object to JSON tools as :
 *  - we can have cycle in object graphs
 *  - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class EnvironmentJSON {

    public final static String ENV_ID          = "environmentID";
    public final static String ENV_VERSION     = "environmentVersion";
    public final static String ENV_NAME        = "environmentName";
    public final static String ENV_DESCRIPTION = "environmentDescription";
    public final static String ENV_OSI_ID      = "environmentOSInstancesID";

    public final static void environment2JSON(Environment environment, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(ENV_ID, environment.getId());
        jgenerator.writeNumberField(ENV_VERSION, environment.getVersion());
        jgenerator.writeStringField(ENV_NAME, environment.getName());
        jgenerator.writeStringField(ENV_DESCRIPTION, environment.getDescription());
        jgenerator.writeArrayFieldStart(ENV_OSI_ID);
        for (OSInstance osi : environment.getOsInstances())
            jgenerator.writeNumber(osi.getId());
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
    }

    public final static void oneEnvironment2JSON(Environment environment, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryBootstrap.getjFactory().createGenerator(outStream, JsonEncoding.UTF8);
        EnvironmentJSON.environment2JSON(environment, jgenerator);
        jgenerator.close();
    }

    public final static void manyEnvironments2JSON(HashSet<Environment> environments, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryBootstrap.getjFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("environments");
        Iterator<Environment> iter = environments.iterator();
        while (iter.hasNext()) {
            Environment current = iter.next();
            EnvironmentJSON.environment2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}