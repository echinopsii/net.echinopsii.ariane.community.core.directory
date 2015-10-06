/**
 * Directory wat
 * Location REST endpoint
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
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.DatacenterJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.rest.CommonRestResponse;
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
import java.util.HashSet;

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.DatacenterJSON.JSONFriendlyDatacenter;

/**
 *
 */
@Path("/directories/common/infrastructure/network/datacenters")
public class DatacenterEndpoint {
    private static final Logger log = LoggerFactory.getLogger(DatacenterEndpoint.class);
    private EntityManager em;

    public static Response datacenterToJSON(Location entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            DatacenterJSON.oneDatacenter2JSON(entity, outStream);
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

    public static Location findDatacenterById(EntityManager em, long id) {
        TypedQuery<Location> findByIdQuery = em.createQuery("SELECT DISTINCT d FROM Location d LEFT JOIN FETCH d.subnets LEFT JOIN FETCH d.routingAreas WHERE d.id = :entityId ORDER BY d.id", Location.class);
        findByIdQuery.setParameter("entityId", id);
        Location entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static Location findDatacenterByName(EntityManager em, String name) {
        TypedQuery<Location> findByNameQuery = em.createQuery("SELECT DISTINCT d FROM Location d LEFT JOIN FETCH d.subnets LEFT JOIN FETCH d.routingAreas WHERE d.name = :entityName ORDER BY d.name", Location.class);
        findByNameQuery.setParameter("entityName", name);
        Location entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyDatacenter jsonFriendlyDatacenter) {
        Location entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if(jsonFriendlyDatacenter.getDatacenterID() !=0)
            entity = findDatacenterById(em, jsonFriendlyDatacenter.getDatacenterID());
        if(entity == null && jsonFriendlyDatacenter.getDatacenterID()!=0){
            commonRestResponse.setErrorMessage("Request Error : provided Location ID " + jsonFriendlyDatacenter.getDatacenterID() +" was not found.");
            return commonRestResponse;
        }
        if(entity == null){
            if(jsonFriendlyDatacenter.getDatacenterName() != null){
                entity = findDatacenterByName(em, jsonFriendlyDatacenter.getDatacenterName());
            }
        }
        if(entity != null){
            if (jsonFriendlyDatacenter.getDatacenterName() !=null) {
                entity.setName(jsonFriendlyDatacenter.getDatacenterName());
            }
            if (jsonFriendlyDatacenter.getDatacenterDescription() != null) {
                entity.setDescription(jsonFriendlyDatacenter.getDatacenterDescription());
            }
            if (jsonFriendlyDatacenter.getDatacenterAddress() != null) {
                entity.setAddress(jsonFriendlyDatacenter.getDatacenterAddress());
            }
            if (jsonFriendlyDatacenter.getDatacenterCountry() != null) {
                entity.setCountry(jsonFriendlyDatacenter.getDatacenterCountry());
            }
            entity.setGpsLatitude(jsonFriendlyDatacenter.getDatacenterGPSLat());
            entity.setGpsLongitude(jsonFriendlyDatacenter.getDatacenterGPSLng());

            if (jsonFriendlyDatacenter.getDatacenterZipCode() != 0) {
                entity.setZipCode(jsonFriendlyDatacenter.getDatacenterZipCode());
            }
            if (jsonFriendlyDatacenter.getDatacenterTown() != null) {
                entity.setTown(jsonFriendlyDatacenter.getDatacenterTown());
            }
            if(jsonFriendlyDatacenter.getDatacenterRoutingAreasID() != null) {
                if (!jsonFriendlyDatacenter.getDatacenterRoutingAreasID().isEmpty()) {
                    for (RoutingArea routingArea : entity.getRoutingAreas()) {
                        if (!jsonFriendlyDatacenter.getDatacenterRoutingAreasID().contains(routingArea.getId())) {
                            entity.getRoutingAreas().remove(routingArea);
                            routingArea.getLocations().remove(entity);
                        }
                    }
                    for (Long routingId : jsonFriendlyDatacenter.getDatacenterRoutingAreasID()) {
                        RoutingArea routingArea = RoutingAreaEndpoint.findRoutingAreaById(em, routingId);
                        if (routingArea != null) {
                            if (!entity.getRoutingAreas().contains(routingArea)) {
                                entity.getRoutingAreas().add(routingArea);
                                routingArea.getLocations().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Locations. Reason : provided RoutingArea ID " + routingId +" was not found.");
                            return  commonRestResponse;
                        }
                    }
                } else {
                    for (RoutingArea routingArea : entity.getRoutingAreas()) {
                        entity.getRoutingAreas().remove(routingArea);
                        routingArea.getLocations().remove(entity);
                    }
                }
            }
            if(jsonFriendlyDatacenter.getDatacenterSubnetsID() != null) {
                if (!jsonFriendlyDatacenter.getDatacenterSubnetsID().isEmpty()) {
                    for (Subnet subnet: entity.getSubnets()) {
                        if (!jsonFriendlyDatacenter.getDatacenterSubnetsID().contains(subnet.getId())) {
                            entity.getSubnets().remove(subnet);
                            subnet.getLocations().remove(entity);
                        }
                    }
                    for (Long subnetId : jsonFriendlyDatacenter.getDatacenterSubnetsID()) {
                        Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetId);
                        if (subnet != null) {
                            if (!entity.getSubnets().contains(subnet)) {
                                entity.getSubnets().add(subnet);
                                subnet.getLocations().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Datacenters. Reason : provided Subnet ID " + subnetId +" was not found.");
                            return  commonRestResponse;
                        }
                    }
                } else {
                    for (Subnet subnet : entity.getSubnets()) {
                        entity.getSubnets().remove(subnet);
                        subnet.getLocations().remove(entity);
                    }
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new Location();
            entity.setNameR(jsonFriendlyDatacenter.getDatacenterName()).setCountryR(jsonFriendlyDatacenter.getDatacenterCountry()).setDescriptionR(jsonFriendlyDatacenter.getDatacenterDescription()).
                   setGpsLatitudeR(jsonFriendlyDatacenter.getDatacenterGPSLat()).setGpsLongitudeR(jsonFriendlyDatacenter.getDatacenterGPSLat()).
                    setTownR(jsonFriendlyDatacenter.getDatacenterTown()).setZipCodeR(jsonFriendlyDatacenter.getDatacenterZipCode()).setAddressR(jsonFriendlyDatacenter.getDatacenterAddress());

            if (jsonFriendlyDatacenter.getDatacenterRoutingAreasID() != null) {
                if (!jsonFriendlyDatacenter.getDatacenterRoutingAreasID().isEmpty()) {
                    for (Long routingId : jsonFriendlyDatacenter.getDatacenterRoutingAreasID()) {
                        RoutingArea routingArea = RoutingAreaEndpoint.findRoutingAreaById(em, routingId);
                        if (routingArea != null) {
                            if (!entity.getRoutingAreas().contains(routingArea)) {
                                entity.getRoutingAreas().add(routingArea);
                                routingArea.getLocations().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to create Datacenters. Reason : provided RoutingArea ID " + routingId +" was not found.");
                            return  commonRestResponse;
                        }
                    }
                }
            }
            if (jsonFriendlyDatacenter.getDatacenterSubnetsID() != null) {
                if (!jsonFriendlyDatacenter.getDatacenterSubnetsID().isEmpty()) {
                    for (Long subnetId : jsonFriendlyDatacenter.getDatacenterSubnetsID()) {
                        Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetId);
                        if (subnet != null) {
                            if (!entity.getSubnets().contains(subnet)) {
                                entity.getSubnets().add(subnet);
                                subnet.getLocations().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to create Datacenters. Reason : provided Subnet ID " + subnetId +" was not found.");
                            return  commonRestResponse;
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
    public Response displayDatacenter(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwLOC:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            Location entity = findDatacenterById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = datacenterToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display datacenters. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllDatacenters() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get datacenters", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwLOC:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Location> results = new HashSet(em.createQuery("SELECT DISTINCT d FROM Location d LEFT JOIN FETCH d.subnets LEFT JOIN FETCH d.routingAreas ORDER BY d.id", Location.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                DatacenterJSON.manyDatacenters2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display datacenters. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getDatacenter(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
            return displayDatacenter(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwLOC:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = datacenterToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createDatacenter(@QueryParam("name")String name, @QueryParam("address")String address, @QueryParam("zipCode")Long zipCode, @QueryParam("town")String town,
                                     @QueryParam("country")String country, @QueryParam("gpsLatitude")Double gpsLat, @QueryParam("gpsLongitude")Double gpsLng,
                                     @QueryParam("description")String description) {
        if (name!=null && address!=null && zipCode!=null && town!=null && country!=null && gpsLat!=null && gpsLng!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:create") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterByName(em, name);
                if (entity==null) {
                    entity = new Location().setNameR(name).setAddressR(address).setZipCodeR(zipCode).setTownR(town).setCountryR(country).setGpsLatitudeR(gpsLat).setGpsLongitudeR(gpsLng).
                                     setDescriptionR(description);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                    }
                }
                Response ret = datacenterToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or address and/or zipCode and/or town and/or country and/or gpsLatitude and/or gpsLongiture" +
                                                                                " are not defined. You must define these parameters.").build();
        }
    }

    @POST
    public Response postDatacenter(@QueryParam("payload") String payload) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update Location : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiNtwLOC:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyDatacenter jsonFriendlyDatacenter = DatacenterJSON.JSON2Datacenter(payload);
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyDatacenter);
            Location entity = (Location) commonRestResponse.getDeserializedObject();
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
                    Response ret = datacenterToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating datacenter " + payload + " : " + t.getMessage()).build();
                }
            } else{
                em.close();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create datacenters. Contact your administrator.").build();
        }
    }
    @GET
    @Path("/delete")
    public Response deleteDatacenter(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        for (Subnet subnet: entity.getSubnets())
                            subnet.getLocations().remove(entity);
                        for (RoutingArea marea :entity.getRoutingAreas())
                            marea.getLocations().remove(entity);
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Location " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting datacenter " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateDatacenterName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    em.getTransaction().begin();
                    entity.setName(name);
                    em.getTransaction().commit();
                    em.close();
                    return Response.status(Status.OK).entity("Location " + id + " has been successfully updated with name " + name).build();
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/fullAddress")
    public Response updateDatacenterFullAddress(@QueryParam("id")Long id, @QueryParam("address")String address, @QueryParam("zipCode")Long zipCode,
                                                @QueryParam("town")String town, @QueryParam("country")String country) {
        if (id!=0 && address!=null && zipCode!=null && town != null && country!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} full address : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id,
                                                                                                address, zipCode, town, country});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setAddressR(address).setZipCodeR(zipCode).setTownR(town).setCountryR(country);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Location " + id + " has been successfully updated with full address " + address + " " + zipCode + " " +
                                                                 town + " " + country).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or address and/or zipCode and/or town and/or country are not defined. " +
                                                                        "You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/gpsCoord")
    public Response updateDatacenterGPSCoord(@QueryParam("id")Long id, @QueryParam("gpsLatitude")Double gpsLat, @QueryParam("gpsLongitude")Double gpsLng) {
        if (id!=0 && gpsLat!=null && gpsLat!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} gps coord : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id,
                                                                                       gpsLat, gpsLng});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setGpsLatitudeR(gpsLat).setGpsLongitude(gpsLng);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Location " + id + " has been successfully updated with gps coord (" + gpsLat + "," + gpsLng + ")").build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or gpsLatitude and/or gpsLongitude are not defined. " +
                                                                                "You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateDatacenterDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescriptionR(description);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Location " + id + " has been successfully updated with description " + description).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/add")
    public Response updateDatacenterAddSubnet(@QueryParam("id")Long id, @QueryParam("subnetID")Long subnetID) {
        if (id!=0 && subnetID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} by adding subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                    if (subnet!=null) {
                        try {
                            em.getTransaction().begin();
                            subnet.getLocations().add(entity);
                            entity.getSubnets().add(subnet);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Location " + id + " has been successfully updated by adding subnet " + subnetID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Subnet " + subnetID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Location " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/delete")
    public Response updateDatacenterDeleteSubnet(@QueryParam("id")Long id, @QueryParam("subnetID")Long subnetID) {
        if (id!=0 && subnetID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} by deleting subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                    if (subnet!=null) {
                        try {
                            em.getTransaction().begin();
                            subnet.getLocations().remove(entity);
                            entity.getSubnets().remove(subnet);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Location " + id + " has been successfully updated by deleting subnet " + subnetID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Subnet " + subnetID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Location " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/routingareas/add")
    public Response updateDatacenterAddRoutingAreas(@QueryParam("id") Long id, @QueryParam("routingareaID") Long rareaID) {
        if (id!=0 && rareaID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} by adding routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, rareaID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    RoutingArea rarea = RoutingAreaEndpoint.findRoutingAreaById(em, rareaID);
                    if (rarea!=null) {
                        try {
                            em.getTransaction().begin();
                            rarea.getLocations().add(entity);
                            entity.getRoutingAreas().add(rarea);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Location " + id + " has been successfully updated by adding routing area " + rareaID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Routing area " + rareaID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Location " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or routingareaID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/routingareas/delete")
    public Response updateDatacenterDeleteRoutingAreas(@QueryParam("id") Long id, @QueryParam("routingareaID") Long mareaID) {
        if (id!=0 && mareaID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update datacenter {} by deleting routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, mareaID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findDatacenterById(em, id);
                if (entity != null) {
                    RoutingArea marea = RoutingAreaEndpoint.findRoutingAreaById(em, mareaID);
                    if (marea!=null) {
                        try {
                            em.getTransaction().begin();
                            marea.getLocations().remove(entity);
                            entity.getRoutingAreas().remove(marea);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Location " + id + " has been successfully updated by deleting routing area " + mareaID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Routing area " + mareaID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Location " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update datacenters. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or routingareaID are not defined. You must define these parameters.").build();
        }
    }
}