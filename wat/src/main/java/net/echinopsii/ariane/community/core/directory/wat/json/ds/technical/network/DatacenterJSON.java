/**
 * Directory WAT
 * Datacenter to JSON tools
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

package net.echinopsii.ariane.community.core.directory.wat.json.ds.technical.network;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Datacenter;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.wat.DirectoryBootstrap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Our own object to JSON tools as :
 *  - we can have cycle in object graphs
 *  - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class DatacenterJSON {

    public final static String DC_ID          = "datacenterID";
    public final static String DC_VERSION     = "datacenterVersion";
    public final static String DC_NAME        = "datacenterName";
    public final static String DC_ADDRESS     = "datacenterAddress";
    public final static String DC_TOWN        = "datacenterTown";
    public final static String DC_ZIPCODE     = "datacenterZipCode";
    public final static String DC_COUNTRY     = "datacenterCountry";
    public final static String DC_GPSLAT      = "datacenterGPSLat";
    public final static String DC_GPSLNG      = "datacenterGPSLng";
    public final static String DC_DESCRIPTION = "datacenterDescription";
    public final static String DC_SUBNETS_ID  = "datacenterSubnetsID";
    public final static String DC_MAREAS_ID   = "datacenterRoutingAreasID";

    public final static void datacenter2JSON(Datacenter datacenter, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(DC_ID, datacenter.getId());
        jgenerator.writeNumberField(DC_VERSION, datacenter.getVersion());
        jgenerator.writeStringField(DC_NAME, datacenter.getName());
        jgenerator.writeStringField(DC_ADDRESS, datacenter.getAddress());
        jgenerator.writeStringField(DC_TOWN, datacenter.getTown());
        jgenerator.writeNumberField(DC_ZIPCODE, datacenter.getZipCode());
        jgenerator.writeStringField(DC_COUNTRY, datacenter.getCountry());
        jgenerator.writeNumberField(DC_GPSLAT, datacenter.getGpsLatitude());
        jgenerator.writeNumberField(DC_GPSLNG, datacenter.getGpsLongitude());
        jgenerator.writeStringField(DC_DESCRIPTION, datacenter.getDescription());
        jgenerator.writeArrayFieldStart(DC_SUBNETS_ID);
        for (Subnet subnet : datacenter.getSubnets())
            jgenerator.writeNumber(subnet.getId());
        jgenerator.writeEndArray();
        jgenerator.writeArrayFieldStart(DC_MAREAS_ID);
        for (RoutingArea marea : datacenter.getRoutingAreas())
            jgenerator.writeNumber(marea.getId());
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
    }

    public final static void oneDatacenter2JSON(Datacenter datacenter, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryBootstrap.getjFactory().createGenerator(outStream, JsonEncoding.UTF8);
        DatacenterJSON.datacenter2JSON(datacenter, jgenerator);
        jgenerator.close();
    }

    public final static void manyDatacenters2JSON(HashSet<Datacenter> datacenters, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryBootstrap.getjFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("datacenters");
        Iterator<Datacenter> iter = datacenters.iterator();
        while (iter.hasNext()) {
            Datacenter current = iter.next();
            DatacenterJSON.datacenter2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}