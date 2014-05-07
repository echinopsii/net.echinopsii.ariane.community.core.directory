/**
 * Directory WAT
 * MulticastArea to JSON tools
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

package com.spectral.cc.core.directory.wat.json.ds.technical.network;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.spectral.cc.core.directory.base.model.technical.network.Datacenter;
import com.spectral.cc.core.directory.base.model.technical.network.MulticastArea;
import com.spectral.cc.core.directory.base.model.technical.network.Subnet;
import com.spectral.cc.core.directory.wat.DirectoryBootstrap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Our own object to JSON tools as :
 *  - we can have cycle in object graphs
 *  - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class MulticastAreaJSON {

    public final static String MAREA_ID          = "multicastAreaID";
    public final static String MAREA_VERSION     = "multicastAreaVersion";
    public final static String MAREA_NAME        = "multicastAreaName";
    public final static String MAREA_DESCRIPTION = "multicastAreaDescription";
    public final static String MAREA_SUBNETS_ID  = "multicastAreaSubnetsID";
    public final static String MAREA_DC_ID       = "multicastAreaDatacentersID";

    public final static void multicastArea2JSON(MulticastArea multicastArea, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(MAREA_ID, multicastArea.getId());
        jgenerator.writeNumberField(MAREA_VERSION, multicastArea.getVersion());
        jgenerator.writeStringField(MAREA_NAME, multicastArea.getName());
        jgenerator.writeStringField(MAREA_DESCRIPTION, multicastArea.getDescription());
        jgenerator.writeArrayFieldStart(MAREA_SUBNETS_ID);
        for (Subnet subnet : multicastArea.getSubnets())
            jgenerator.writeNumber(subnet.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(MAREA_DC_ID);
        for (Datacenter dc : multicastArea.getDatacenters())
            jgenerator.writeNumber(dc.getId());
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
    }

    public final static void oneMulticastArea2JSON(MulticastArea multicastArea, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryBootstrap.getjFactory().createGenerator(outStream, JsonEncoding.UTF8);
        MulticastAreaJSON.multicastArea2JSON(multicastArea, jgenerator);
        jgenerator.close();
    }

    public final static void manyMulticastAreas2JSON(HashSet<MulticastArea> multicastAreas, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryBootstrap.getjFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("multicastAreas");
        Iterator<MulticastArea> iter = multicastAreas.iterator();
        while (iter.hasNext()) {
            MulticastArea current = iter.next();
            MulticastAreaJSON.multicastArea2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}