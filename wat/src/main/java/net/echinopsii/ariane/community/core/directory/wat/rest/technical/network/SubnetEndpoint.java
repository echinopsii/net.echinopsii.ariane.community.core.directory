/**
 * Directory wat
 * Subnet REST endpoint
 * Copyright (C) 2013 Mathilde Ffrench
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
package net.echinopsii.ariane.community.core.directory.wat.rest.technical.network;

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Location;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.SubnetJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.rest.CommonRestResponse;
import net.echinopsii.ariane.community.core.directory.wat.rest.technical.system.OSInstanceEndpoint;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.SubnetJSON.JSONFriendlySubnet;

/**
 *
 */
@Path("/directories/common/infrastructure/network/subnets")
public class SubnetEndpoint {
    private static final Logger log = LoggerFactory.getLogger(SubnetEndpoint.class);
    private EntityManager em;

    public static Response subnetToJSON(Subnet entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            SubnetJSON.oneSubnet2JSON(entity, outStream);
            result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
            ret = Response.status(Status.OK).entity(result).build();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            result = e.getMessage();
            ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
        }
        return ret;
    }

    public static Subnet findSubnetById(EntityManager em, long id) {
        TypedQuery<Subnet> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.locations LEFT JOIN FETCH l.rarea WHERE l.id = :entityId ORDER BY l.id", Subnet.class);
        findByIdQuery.setParameter("entityId", id);
        Subnet entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static Subnet findSubnetByName(EntityManager em, String name) {
        TypedQuery<Subnet> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.locations LEFT JOIN FETCH l.rarea WHERE l.name = :entityName ORDER BY l.name", Subnet.class);
        findByNameQuery.setParameter("entityName", name);
        Subnet entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static RoutingArea findRoutingAreaByID(EntityManager em, long id) {
        TypedQuery<RoutingArea> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM RoutingArea l WHERE l.id = :entityID ORDER BY l.name", RoutingArea.class);
        findByNameQuery.setParameter("entityID", id);
        RoutingArea entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static String getAvailableRoutingArea(EntityManager em) {
        List<RoutingArea> resultList = em.createQuery("SELECT DISTINCT l FROM RoutingArea l ORDER BY l.name", RoutingArea.class).getResultList();
        List<Long> routingAreaNames = new ArrayList<>();
        for (RoutingArea rarea : resultList)
            routingAreaNames.add(rarea.getId());
        return routingAreaNames.toString();
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlySubnet jsonFriendlySubnet) {
        Subnet entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if(jsonFriendlySubnet.getSubnetID() !=0)
            entity = findSubnetById(em, jsonFriendlySubnet.getSubnetID());
        if(entity == null && jsonFriendlySubnet.getSubnetID()!=0){
            commonRestResponse.setErrorMessage("Request Error : provided Subnet ID " + jsonFriendlySubnet.getSubnetID() +" was not found.");
            return commonRestResponse;
        }
        if(entity == null){
            if(jsonFriendlySubnet.getSubnetName() != null){
                entity = findSubnetByName(em, jsonFriendlySubnet.getSubnetName());
            }
        }
        if(entity != null) {
            if (jsonFriendlySubnet.getSubnetName() !=null) {
                entity.setName(jsonFriendlySubnet.getSubnetName());
            }
            if (jsonFriendlySubnet.getSubnetMask() != null) {
                entity.setSubnetMask(jsonFriendlySubnet.getSubnetMask());
            }
            if (jsonFriendlySubnet.getSubnetDescription() != null) {
                entity.setDescription(jsonFriendlySubnet.getSubnetDescription());
            }
            if (jsonFriendlySubnet.getSubnetIP() != null) {
                entity.setSubnetIP(jsonFriendlySubnet.getSubnetIP());
            }
            if (jsonFriendlySubnet.getSubnetMask() != null) {
                entity.setSubnetMask(jsonFriendlySubnet.getSubnetMask());
            }
            if (jsonFriendlySubnet.getSubnetRoutingAreaID() != 0) {
                RoutingArea routingArea = RoutingAreaEndpoint.findRoutingAreaById(em, jsonFriendlySubnet.getSubnetRoutingAreaID());
                if (routingArea != null) {
                    if (entity.getRarea() != null)
                        entity.getRarea().getSubnets().remove(entity);
                    entity.setRarea(routingArea);
                    routingArea.getSubnets().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to update Subnet. Reason : provided Routing Area ID " + jsonFriendlySubnet.getSubnetRoutingAreaID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if(jsonFriendlySubnet.getSubnetOSInstancesID() != null) {
                if (!jsonFriendlySubnet.getSubnetOSInstancesID().isEmpty()) {
                    for (OSInstance osInstance : new HashSet<>(entity.getOsInstances())) {
                        if (!jsonFriendlySubnet.getSubnetOSInstancesID().contains(osInstance.getId())) {
                            entity.getOsInstances().remove(osInstance);
                            osInstance.getNetworkSubnets().remove(entity);
                        }
                    }
                    for (Long osiId : jsonFriendlySubnet.getSubnetOSInstancesID()) {
                        OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiId);
                        if (osInstance != null) {
                            if (!entity.getOsInstances().contains(osInstance)) {
                                entity.getOsInstances().add(osInstance);
                                osInstance.getNetworkSubnets().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Subnet. Reason : provided OS Instance ID " + osiId +" was not found.");
                            return commonRestResponse;
                        }
                    }
                } else {
                    for (OSInstance osInstance: new HashSet<>(entity.getOsInstances())) {
                        entity.getOsInstances().remove(osInstance);
                        osInstance.getNetworkSubnets().remove(entity);
                    }
                }
            }
            if(jsonFriendlySubnet.getSubnetLocationsID() != null) {
                if (!jsonFriendlySubnet.getSubnetLocationsID().isEmpty()) {
                    for (Location location : new HashSet<>(entity.getLocations())) {
                        if (!jsonFriendlySubnet.getSubnetLocationsID().contains(location.getId())) {
                            entity.getLocations().remove(location);
                            location.getSubnets().remove(entity);
                        }
                    }
                    for (Long locId : jsonFriendlySubnet.getSubnetLocationsID()) {
                        Location location = LocationEndpoint.findLocationById(em, locId);
                        if (location != null) {
                            if (!entity.getLocations().contains(location)) {
                                entity.getLocations().add(location);
                                location.getSubnets().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Subnet. Reason : provided Location ID " + locId +" was not found.");
                            return commonRestResponse;
                        }
                    }
                } else {
                    for (Location location : new HashSet<>(entity.getLocations())) {
                        entity.getLocations().remove(location);
                        location.getSubnets().remove(entity);
                    }
                }
            }
            if(jsonFriendlySubnet.getSubnetIPAddressesID() != null) {
                if (!jsonFriendlySubnet.getSubnetIPAddressesID().isEmpty()) {
                    for (IPAddress ipAddress : new HashSet<>(entity.getIpAddresses())) {
                        if (!jsonFriendlySubnet.getSubnetIPAddressesID().contains(ipAddress.getId())) {
                            entity.getIpAddresses().remove(ipAddress);
                            ipAddress.setNetworkSubnet(null);
                        }
                    }
                    for (Long ipaId : jsonFriendlySubnet.getSubnetIPAddressesID()) {
                        IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, ipaId);
                        if (ipAddress != null) {
                            if (!entity.getIpAddresses().contains(ipAddress)) {
                                entity.getIpAddresses().add(ipAddress);
                                ipAddress.setNetworkSubnet(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Subnet. Reason : provided IPAddress ID " + ipaId +" was not found.");
                            return commonRestResponse;
                        }
                    }
                } else {
                    for (IPAddress ipAddress : new HashSet<>(entity.getIpAddresses())) {
                        entity.getIpAddresses().remove(ipAddress);
                        ipAddress.setNetworkSubnet(null);
                    }
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new Subnet();
            entity.setNameR(jsonFriendlySubnet.getSubnetName()).setDescriptionR(jsonFriendlySubnet.getSubnetDescription()).setSubnetIPR(jsonFriendlySubnet.getSubnetIP())
                  .setSubnetMaskR(jsonFriendlySubnet.getSubnetMask());
            if (jsonFriendlySubnet.getSubnetRoutingAreaID() != 0) {
                RoutingArea routingArea = RoutingAreaEndpoint.findRoutingAreaById(em, jsonFriendlySubnet.getSubnetRoutingAreaID());
                if (routingArea != null) {
                    if (entity.getRarea() != null)
                        entity.getRarea().getSubnets().remove(entity);
                    entity.setRarea(routingArea);
                    routingArea.getSubnets().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create Subnet. Reason : provided Routing Area ID " + jsonFriendlySubnet.getSubnetRoutingAreaID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if(jsonFriendlySubnet.getSubnetOSInstancesID() != null) {
                if (!jsonFriendlySubnet.getSubnetOSInstancesID().isEmpty()) {
                    for (Long osiId : jsonFriendlySubnet.getSubnetOSInstancesID()) {
                        OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiId);
                        if (osInstance != null) {
                            if (!entity.getOsInstances().contains(osInstance)) {
                                entity.getOsInstances().add(osInstance);
                                osInstance.getNetworkSubnets().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Subnet. Reason : provided OS Instance ID " + osiId + " was not found.");
                            return commonRestResponse;
                        }
                    }
                }
            }
            if(jsonFriendlySubnet.getSubnetLocationsID() != null) {
                if (!jsonFriendlySubnet.getSubnetLocationsID().isEmpty()) {
                    for (Long locId : jsonFriendlySubnet.getSubnetLocationsID()) {
                        Location location = LocationEndpoint.findLocationById(em, locId);
                        if (location != null) {
                            if (!entity.getLocations().contains(location)) {
                                entity.getLocations().add(location);
                                location.getSubnets().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Subnet. Reason : provided Location ID " + locId + " was not found.");
                            return commonRestResponse;
                        }
                    }
                }
            }
            if(jsonFriendlySubnet.getSubnetIPAddressesID() != null) {
                if (!jsonFriendlySubnet.getSubnetIPAddressesID().isEmpty()) {
                    for (Long ipaId : jsonFriendlySubnet.getSubnetIPAddressesID()) {
                        IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, ipaId);
                        if (ipAddress != null) {
                            if (!entity.getIpAddresses().contains(ipAddress)) {
                                entity.getIpAddresses().add(ipAddress);
                                ipAddress.setNetworkSubnet(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Subnet. Reason : provided IPAddress ID " + ipaId + " was not found.");
                            return commonRestResponse;
                        }
                    }
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        }
        return commonRestResponse;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displaySubnet(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwSubnet:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            Subnet entity = findSubnetById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = subnetToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display subnets. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllSubnets() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get subnets", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwSubnet:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Subnet> results = new HashSet(em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.locations LEFT JOIN FETCH l.rarea ORDER BY l.id", Subnet.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                SubnetJSON.manySubnets2JSON(results, outStream);
                result = ToolBox.getOuputStreamContent(outStream, "UTF-8");
                ret = Response.status(Status.OK).entity(result).build();
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                result = e.getMessage();
                ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity(result).build();
            } finally {
                em.close();
                return ret;
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display subnets. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getSubnet(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
            return displaySubnet(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwSubnet:display") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = subnetToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createSubnet(@QueryParam("name")String name, @QueryParam("subnetIP")String subnetIP, @QueryParam("subnetMask")String subnetMask,
                                 @QueryParam("routingArea")int routingAreaID, @QueryParam("description")String description) {
        if (name!=null && subnetIP!=null && subnetMask!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create subnet : ({},{},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, subnetIP, subnetMask, routingAreaID, description});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetByName(em, name);
                if (entity==null) {
                    RoutingArea routingArea = findRoutingAreaByID(em, routingAreaID);
                    if (routingArea!=null) {
                        entity = new Subnet().setNameR(name).setDescriptionR(description).setSubnetIPR(subnetIP).setSubnetMaskR(subnetMask).setRareaR(routingArea);
                        try {
                            em.getTransaction().begin();
                            em.persist(entity);
                            em.getTransaction().commit();
                        } catch (Throwable t) {
                            t.printStackTrace();
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        log.error("Wrong routing area " + routingAreaID + ". Available routing areas are : " + getAvailableRoutingArea(em));
                        Response ret = Response.status(Status.INTERNAL_SERVER_ERROR).
                                entity("Wrong routing area " + routingAreaID + ". Available routing areas are : " + getAvailableRoutingArea(em)).build();
                        em.close();
                        return ret;
                    }
                }

                Response ret = subnetToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: name and/or subnetIP and/or subnetMask and/or type are not defined. " +
                                                                        "You must define these parameters.").build();
        }
    }

    @POST
    public Response postSubnet(@QueryParam("payload") String payload) {

        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update Subnet : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiNtwSubnet:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlySubnet jsonFriendlySubnet = null;
            try {
                jsonFriendlySubnet = SubnetJSON.JSON2Subnet(payload);
            } catch (IOException e) {
                log.error("Problem while deserializing payload : " + payload);
                e.printStackTrace();
                return Response.status(Status.BAD_REQUEST).entity("Problem while deserializing payload : " + payload).build();
            }
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlySubnet);
            Subnet entity = (Subnet) commonRestResponse.getDeserializedObject();
            if (entity != null) {
                try {
                    em.getTransaction().begin();
                    if (entity.getId() == null){
                        em.persist(entity);
                        em.flush();
                        em.getTransaction().commit();
                    } else {
                        em.merge(entity);
                        em.flush();
                        em.getTransaction().commit();
                    }
                    Response ret = subnetToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    t.printStackTrace();
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating subnet " + payload + " : " + t.getMessage()).build();
                }
            } else{
                em.close();
                log.error(commonRestResponse.getErrorMessage());
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create subnets. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteSubnet(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        for (IPAddress ipAddress : entity.getIpAddresses()) {
                            if (ipAddress.getNic() != null) {
                                ipAddress.getNic().setIpAddressR(null);
                                ipAddress.setNic(null);
                            }
                            if (ipAddress.getOsInstance()!=null) {
                                ipAddress.getOsInstance().getIpAddresses().remove(ipAddress);
                                ipAddress.setOsInstance(null);
                            }
                            em.remove(ipAddress);
                        }
                        entity.getIpAddresses().clear();
                        em.flush();
                        em.getTransaction().commit();
                        em.getTransaction().begin();
                        for (Location loc : entity.getLocations())
                            loc.getSubnets().remove(entity);
                        for (OSInstance osi : entity.getOsInstances())
                            osi.getNetworkSubnets().remove(entity);
                        if (entity.getRarea()!=null)
                            entity.getRarea().getSubnets().remove(entity);
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Subnet " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating subnet " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id is no defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateSubnetName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateSubnetDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescription(description);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with desription " + description).build();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnetip")
    public Response updateSubnetIP(@QueryParam("id")Long id, @QueryParam("subnetIP")String subnetIP) {
        if (id!=0 && subnetIP!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} IP : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetIP});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setSubnetIP(subnetIP);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with IP " + subnetIP).build();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnetmask")
    public Response updateSubnetMask(@QueryParam("id")Long id, @QueryParam("subnetMask")String subnetMask) {
        if (id!=0 && subnetMask!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} mask : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetMask});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setSubnetMask(subnetMask);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with mask " + subnetMask).build();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/routingarea")
    public Response updateSubnetRoutingArea(@QueryParam("id") Long id, @QueryParam("routingareaID") Long rareaID) {
        if (id!=0 && rareaID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, rareaID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    RoutingArea rarea = RoutingAreaEndpoint.findRoutingAreaById(em, rareaID);
                    if (rarea!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getRarea()!=null)
                                entity.getRarea().getSubnets().remove(entity);
                            rarea.getSubnets().add(entity);
                            entity.setRarea(rarea);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with routing area " + rareaID).build();
                        } catch (Throwable t) {
                            t.printStackTrace();
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Routing Area " + rareaID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/add")
    public Response updateSubnetAddOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by adding os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance!=null) {
                        try {
                            em.getTransaction().begin();
                            osInstance.getNetworkSubnets().add(entity);
                            entity.getOsInstances().add(osInstance);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated by adding os instance " + osiID).build();
                        } catch (Throwable t) {
                            t.printStackTrace();
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/delete")
    public Response updateSubnetDeleteOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by deleting os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance!=null) {
                        try {
                            em.getTransaction().begin();
                            osInstance.getNetworkSubnets().remove(entity);
                            entity.getOsInstances().remove(osInstance);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated by removing os instance " + osiID).build();
                        } catch (Throwable t) {
                            t.printStackTrace();
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/locations/add")
    public Response updateSubnetAddLocation(@QueryParam("id") Long id, @QueryParam("locationID") Long locID) {
        if (id!=0 && locID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by adding location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, locID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    Location location = LocationEndpoint.findLocationById(em, locID);
                    if (location !=null) {
                        try {
                            em.getTransaction().begin();
                            location.getSubnets().add(entity);
                            entity.getLocations().add(location);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated by adding location " + locID).build();
                        } catch (Throwable t) {
                            t.printStackTrace();
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Location " + locID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/locations/delete")
    public Response updateSubnetDeleteLocation(@QueryParam("id") Long id, @QueryParam("locationID") Long locID) {
        if (id!=0 && locID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by adding location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, locID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    Location location = LocationEndpoint.findLocationById(em, locID);
                    if (location !=null) {
                        try {
                            em.getTransaction().begin();
                            location.getSubnets().remove(entity);
                            entity.getLocations().remove(location);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated by removing location " + locID).build();
                        } catch (Throwable t) {
                            t.printStackTrace();
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Location " + locID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.BAD_REQUEST).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

}