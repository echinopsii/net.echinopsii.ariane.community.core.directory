/**
 * Directory WAT
 * Subnet to JSON tools
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
import net.echinopsii.ariane.community.core.directory.base.iPojo.DirectoryTreeMenuRootsRegistryImpl;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Our own object to JSON tools as :
 *  - we can have cycle in object graphs
 *  - we still want to have references to linked objects through IDs (and so we don't want @XmlTransient or @JsonIgnore)
 */
public class IPAddressJSON {

    public final static String IPADDRESS_ID             = "ipAddressID";
    public final static String IPADDRESS_VERSION        = "ipAddressVersion";
    public final static String IPADDRESS_IPA            = "ipAddressIPA";
    public final static String IPADDRESS_FQDN           = "ipAddressFQDN";
    public final static String IPADDRESS_SUBNET_ID      = "ipAddressSubnetID";
    public final static String IPADDRESS_OSI_ID         = "ipAddressOSInstanceID";


    public final static void ipAddress2JSON(IPAddress ipAddress, JsonGenerator jgenerator) throws IOException {
        jgenerator.writeStartObject();
        jgenerator.writeNumberField(IPADDRESS_ID, ipAddress.getId());
        jgenerator.writeNumberField(IPADDRESS_VERSION, ipAddress.getVersion());
        jgenerator.writeStringField(IPADDRESS_IPA, ipAddress.getIpAddress());
        jgenerator.writeStringField(IPADDRESS_FQDN, ipAddress.getFqdn());
        jgenerator.writeNumberField(IPADDRESS_OSI_ID, ((ipAddress.getOsInstance()!=null)?ipAddress.getOsInstance().getId():-1));
        jgenerator.writeNumberField(IPADDRESS_SUBNET_ID, ((ipAddress.getNetworkSubnet()!=null)?ipAddress.getNetworkSubnet().getId():-1));

        jgenerator.writeEndObject();
    }

    public final static void oneIPAddress2JSON(IPAddress ipAddress, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        IPAddressJSON.ipAddress2JSON(ipAddress, jgenerator);
        jgenerator.close();
    }

    public final static void manyIPAddresses2JSON(HashSet<IPAddress> ipAddresses, ByteArrayOutputStream outStream) throws IOException {
        JsonGenerator jgenerator = DirectoryTreeMenuRootsRegistryImpl.getJFactory().createGenerator(outStream, JsonEncoding.UTF8);
        jgenerator.writeStartObject();
        jgenerator.writeArrayFieldStart("ipAddresses");
        Iterator<IPAddress> iter = ipAddresses.iterator();
        while (iter.hasNext()) {
            IPAddress current = iter.next();
            IPAddressJSON.ipAddress2JSON(current, jgenerator);
        }
        jgenerator.writeEndArray();
        jgenerator.writeEndObject();
        jgenerator.close();
    }
}