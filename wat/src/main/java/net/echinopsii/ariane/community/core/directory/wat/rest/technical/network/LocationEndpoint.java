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

import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.LocationJSON;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Location;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.RoutingArea;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
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

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.LocationJSON.JSONFriendlyLocation;

/**
 *
 */
@Path("/directories/common/infrastructure/network/locations")
public class LocationEndpoint {
    private static final Logger log = LoggerFactory.getLogger(LocationEndpoint.class);
    private EntityManager em;

    public static Response locationToJSON(Location entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            LocationJSON.oneLocation2JSON(entity, outStream);
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

    public static Location findLocationById(EntityManager em, long id) {
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

    public static Location findLocationByName(EntityManager em, String name) {
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

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyLocation jsonFriendlyLocation) {
        Location entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if(jsonFriendlyLocation.getLocationID() !=0)
            entity = findLocationById(em, jsonFriendlyLocation.getLocationID());
        if(entity == null && jsonFriendlyLocation.getLocationID()!=0){
            commonRestResponse.setErrorMessage("Request Error : provided Location ID " + jsonFriendlyLocation.getLocationID() +" was not found.");
            return commonRestResponse;
        }
        if(entity == null){
            if(jsonFriendlyLocation.getLocationName() != null){
                entity = findLocationByName(em, jsonFriendlyLocation.getLocationName());
            }
        }
        if(entity != null){
            if (jsonFriendlyLocation.getLocationName() !=null) {
                entity.setName(jsonFriendlyLocation.getLocationName());
            }
            if (jsonFriendlyLocation.getLocationDescription() != null) {
                entity.setDescription(jsonFriendlyLocation.getLocationDescription());
            }
            if (jsonFriendlyLocation.getLocationAddress() != null) {
                entity.setAddress(jsonFriendlyLocation.getLocationAddress());
            }
            if (jsonFriendlyLocation.getLocationCountry() != null) {
                entity.setCountry(jsonFriendlyLocation.getLocationCountry());
            }
            entity.setGpsLatitude(jsonFriendlyLocation.getLocationGPSLat());
            entity.setGpsLongitude(jsonFriendlyLocation.getLocationGPSLng());

            if (jsonFriendlyLocation.getLocationZipCode() != 0) {
                entity.setZipCode(jsonFriendlyLocation.getLocationZipCode());
            }
            if (jsonFriendlyLocation.getLocationTown() != null) {
                entity.setTown(jsonFriendlyLocation.getLocationTown());
            }
            if (jsonFriendlyLocation.getLocationType() != null) {
                entity.setType(jsonFriendlyLocation.getLocationType());
            }
            if(jsonFriendlyLocation.getLocationRoutingAreasID() != null) {
                if (!jsonFriendlyLocation.getLocationRoutingAreasID().isEmpty()) {
                    for (RoutingArea routingArea : entity.getRoutingAreas()) {
                        if (!jsonFriendlyLocation.getLocationRoutingAreasID().contains(routingArea.getId())) {
                            entity.getRoutingAreas().remove(routingArea);
                            routingArea.getLocations().remove(entity);
                        }
                    }
                    for (Long routingId : jsonFriendlyLocation.getLocationRoutingAreasID()) {
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
            if(jsonFriendlyLocation.getLocationSubnetsID() != null) {
                if (!jsonFriendlyLocation.getLocationSubnetsID().isEmpty()) {
                    for (Subnet subnet: entity.getSubnets()) {
                        if (!jsonFriendlyLocation.getLocationSubnetsID().contains(subnet.getId())) {
                            entity.getSubnets().remove(subnet);
                            subnet.getLocations().remove(entity);
                        }
                    }
                    for (Long subnetId : jsonFriendlyLocation.getLocationSubnetsID()) {
                        Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetId);
                        if (subnet != null) {
                            if (!entity.getSubnets().contains(subnet)) {
                                entity.getSubnets().add(subnet);
                                subnet.getLocations().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Locations. Reason : provided Subnet ID " + subnetId +" was not found.");
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
            entity.setNameR(jsonFriendlyLocation.getLocationName()).setCountryR(jsonFriendlyLocation.getLocationCountry()).setDescriptionR(jsonFriendlyLocation.getLocationDescription()).
                   setGpsLatitudeR(jsonFriendlyLocation.getLocationGPSLat()).setGpsLongitudeR(jsonFriendlyLocation.getLocationGPSLat()).
                    setTownR(jsonFriendlyLocation.getLocationTown()).setZipCodeR(jsonFriendlyLocation.getLocationZipCode()).setAddressR(jsonFriendlyLocation.getLocationAddress()).setTypeR(jsonFriendlyLocation.getLocationType());

            if (jsonFriendlyLocation.getLocationRoutingAreasID() != null) {
                if (!jsonFriendlyLocation.getLocationRoutingAreasID().isEmpty()) {
                    for (Long routingId : jsonFriendlyLocation.getLocationRoutingAreasID()) {
                        RoutingArea routingArea = RoutingAreaEndpoint.findRoutingAreaById(em, routingId);
                        if (routingArea != null) {
                            if (!entity.getRoutingAreas().contains(routingArea)) {
                                entity.getRoutingAreas().add(routingArea);
                                routingArea.getLocations().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to create Locations. Reason : provided RoutingArea ID " + routingId +" was not found.");
                            return  commonRestResponse;
                        }
                    }
                }
            }
            if (jsonFriendlyLocation.getLocationSubnetsID() != null) {
                if (!jsonFriendlyLocation.getLocationSubnetsID().isEmpty()) {
                    for (Long subnetId : jsonFriendlyLocation.getLocationSubnetsID()) {
                        Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetId);
                        if (subnet != null) {
                            if (!entity.getSubnets().contains(subnet)) {
                                entity.getSubnets().add(subnet);
                                subnet.getLocations().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to create Locations. Reason : provided Subnet ID " + subnetId +" was not found.");
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
    public Response displayLocation(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwLOC:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            Location entity = findLocationById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = locationToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display locations. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllLocations() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get locations", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwLOC:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Location> results = new HashSet(em.createQuery("SELECT DISTINCT d FROM Location d LEFT JOIN FETCH d.subnets LEFT JOIN FETCH d.routingAreas ORDER BY d.id", Location.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                LocationJSON.manyLocations2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display locations. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getLocation(@QueryParam("name") String name, @QueryParam("id") long id) {
        if (id!=0) {
            return displayLocation(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwLOC:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = locationToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createLocation(@QueryParam("name") String name, @QueryParam("address") String address, @QueryParam("zipCode") Long zipCode, @QueryParam("town") String town,
                                   @QueryParam("country") String country, @QueryParam("gpsLatitude") Double gpsLat, @QueryParam("gpsLongitude") Double gpsLng,
                                   @QueryParam("description") String description, @QueryParam("type") String type) {
        if (name!=null && address!=null && zipCode!=null && town!=null && country!=null && gpsLat!=null && gpsLng!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:create") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationByName(em, name);
                if (entity==null) {
                    entity = new Location().setNameR(name).setAddressR(address).setZipCodeR(zipCode).setTownR(town).setCountryR(country).setGpsLatitudeR(gpsLat).setGpsLongitudeR(gpsLng).
                                     setDescriptionR(description).setTypeR(type);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating location " + entity.getName() + " : " + t.getMessage()).build();
                    }
                }
                Response ret = locationToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or address and/or zipCode and/or town and/or country and/or gpsLatitude and/or gpsLongiture" +
                                                                                " are not defined. You must define these parameters.").build();
        }
    }

    @POST
    public Response postLocation(@QueryParam("payload") String payload) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update Location : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiNtwLOC:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyLocation jsonFriendlyLocation = LocationJSON.JSON2Location(payload);
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyLocation);
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
                    Response ret = locationToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating location " + payload + " : " + t.getMessage()).build();
                }
            } else{
                em.close();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create locations. Contact your administrator.").build();
        }
    }
    @GET
    @Path("/delete")
    public Response deleteLocation(@QueryParam("id") Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting location " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateLocationName(@QueryParam("id") Long id, @QueryParam("name") String name) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/fullAddress")
    public Response updateLocationFullAddress(@QueryParam("id") Long id, @QueryParam("address") String address, @QueryParam("zipCode") Long zipCode,
                                              @QueryParam("town") String town, @QueryParam("country") String country) {
        if (id!=0 && address!=null && zipCode!=null && town != null && country!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} full address : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id,
                                                                                                address, zipCode, town, country});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or address and/or zipCode and/or town and/or country are not defined. " +
                                                                        "You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/gpsCoord")
    public Response updateLocationGPSCoord(@QueryParam("id") Long id, @QueryParam("gpsLatitude") Double gpsLat, @QueryParam("gpsLongitude") Double gpsLng) {
        if (id!=0 && gpsLat!=null && gpsLat!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} gps coord : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id,
                                                                                       gpsLat, gpsLng});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or gpsLatitude and/or gpsLongitude are not defined. " +
                                                                                "You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateLocationDescription(@QueryParam("id") Long id, @QueryParam("description") String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/type")
    public Response updateLocationType(@QueryParam("id") Long id, @QueryParam("type") String type) {
        if (id!=0 && type!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} full address : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id,
                    type});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setType(type);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Location " + id + " has been successfully updated with type " + type).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or type are not defined. " +
                    "You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/add")
    public Response updateLocationAddSubnet(@QueryParam("id") Long id, @QueryParam("subnetID") Long subnetID) {
        if (id!=0 && subnetID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} by adding subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/delete")
    public Response updateLocationDeleteSubnet(@QueryParam("id") Long id, @QueryParam("subnetID") Long subnetID) {
        if (id!=0 && subnetID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} by deleting subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/routingareas/add")
    public Response updateLocationAddRoutingAreas(@QueryParam("id") Long id, @QueryParam("routingareaID") Long rareaID) {
        if (id!=0 && rareaID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} by adding routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, rareaID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or routingareaID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/routingareas/delete")
    public Response updateLocationDeleteRoutingAreas(@QueryParam("id") Long id, @QueryParam("routingareaID") Long mareaID) {
        if (id!=0 && mareaID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update location {} by deleting routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, mareaID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwLOC:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Location entity = findLocationById(em, id);
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
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update locations. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or routingareaID are not defined. You must define these parameters.").build();
        }
    }
}