/**
 * Directory wat
 * Routing Area REST endpoint
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
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.RoutingAreaJSON;
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

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.RoutingAreaJSON.JSONFriendlyRoutingArea;

/**
 *
 */
@Path("/directories/common/infrastructure/network/routingareas")
public class RoutingAreaEndpoint {
    private static final Logger log = LoggerFactory.getLogger(RoutingAreaEndpoint.class);
    private EntityManager em;

    public static Response routingAreaToJSON(RoutingArea entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            RoutingAreaJSON.oneRoutingArea2JSON(entity, outStream);
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

    public static RoutingArea findRoutingAreaById(EntityManager em, long id) {
        TypedQuery<RoutingArea> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM RoutingArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.locations WHERE m.id = :entityId ORDER BY m.id", RoutingArea.class);
        findByIdQuery.setParameter("entityId", id);
        RoutingArea entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static RoutingArea findRoutingAreaByName(EntityManager em, String name) {
        TypedQuery<RoutingArea> findByNameQuery = em.createQuery("SELECT DISTINCT m FROM RoutingArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.locations WHERE m.name = :entityName ORDER BY m.name", RoutingArea.class);
        findByNameQuery.setParameter("entityName", name);
        RoutingArea entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyRoutingArea jsonFriendlyRoutingArea) {
        RoutingArea entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if(jsonFriendlyRoutingArea.getRoutingAreaID() !=0)
            entity = findRoutingAreaById(em, jsonFriendlyRoutingArea.getRoutingAreaID());
        if(entity == null && jsonFriendlyRoutingArea.getRoutingAreaID()!=0){
            commonRestResponse.setErrorMessage("Request Error : provided Routing area ID " + jsonFriendlyRoutingArea.getRoutingAreaID() +" was not found.");
            return commonRestResponse;
        }
        if(entity == null){
            if(jsonFriendlyRoutingArea.getRoutingAreaName() != null){
                entity = findRoutingAreaByName(em, jsonFriendlyRoutingArea.getRoutingAreaName());
            }
        }
        if(entity != null) {
            if (jsonFriendlyRoutingArea.getRoutingAreaName() !=null) {
                entity.setName(jsonFriendlyRoutingArea.getRoutingAreaName());
            }
            if (jsonFriendlyRoutingArea.getRoutingAreaType() != null) {
                entity.setType(jsonFriendlyRoutingArea.getRoutingAreaType());
            }
            if (jsonFriendlyRoutingArea.getRoutingAreaMulticast() != null) {
                entity.setMulticast(jsonFriendlyRoutingArea.getRoutingAreaMulticast());
            }
            if (jsonFriendlyRoutingArea.getRoutingAreaDescription() != null) {
                entity.setDescription(jsonFriendlyRoutingArea.getRoutingAreaDescription());
            }
            if(jsonFriendlyRoutingArea.getRoutingAreaLocationsID() != null) {
                if (!jsonFriendlyRoutingArea.getRoutingAreaLocationsID().isEmpty()) {
                    for (Location location : entity.getLocations()) {
                        if (!jsonFriendlyRoutingArea.getRoutingAreaLocationsID().contains(location.getId())) {
                            entity.getLocations().remove(location);
                            location.getRoutingAreas().remove(entity);
                        }
                    }
                    for (Long locId : jsonFriendlyRoutingArea.getRoutingAreaLocationsID()) {
                        Location location = LocationEndpoint.findLocationById(em, locId);
                        if (location != null) {
                            if (!entity.getLocations().contains(location)) {
                                entity.getLocations().add(location);
                                location.getRoutingAreas().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Routing Area. Reason : provided Location ID " + locId +" was not found.");
                            return commonRestResponse;
                        }
                    }
                } else {
                    for (Location location : entity.getLocations()) {
                        entity.getLocations().remove(location);
                        location.getRoutingAreas().remove(entity);
                    }
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new RoutingArea();
            entity.setNameR(jsonFriendlyRoutingArea.getRoutingAreaName()).setDescriptionR(jsonFriendlyRoutingArea.getRoutingAreaDescription())
                  .setMulticastR(jsonFriendlyRoutingArea.getRoutingAreaMulticast()).setTypeR(jsonFriendlyRoutingArea.getRoutingAreaType());

            if(jsonFriendlyRoutingArea.getRoutingAreaLocationsID() != null) {
                if (!jsonFriendlyRoutingArea.getRoutingAreaLocationsID().isEmpty()) {
                    for (Long locId : jsonFriendlyRoutingArea.getRoutingAreaLocationsID()) {
                        Location location = LocationEndpoint.findLocationById(em, locId);
                        if (location != null) {
                            if (!entity.getLocations().contains(location)) {
                                entity.getLocations().add(location);
                                location.getRoutingAreas().add(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Routing area. Reason : provided Location ID " + locId + " was not found.");
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
    public Response displayRoutingArea(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwRarea:display") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            RoutingArea entity = findRoutingAreaById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = routingAreaToJSON(entity);
            em.close();
            return ret;
        } else
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display routing areas. Contact your administrator.").build();
    }

    @GET
    public Response displayAllRoutingAreas() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get routing areas", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwRarea:display") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<RoutingArea> results = new HashSet(em.createQuery("SELECT DISTINCT m FROM RoutingArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.locations ORDER BY m.id", RoutingArea.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                RoutingAreaJSON.manyRoutingAreas2JSON(results, outStream);
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
        } else
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display routing areas. Contact your administrator.").build();
    }

    @GET
    @Path("/get")
    public Response getRoutingArea(@QueryParam("name") String name, @QueryParam("id") long id) {
        if (id != 0) {
            return displayRoutingArea(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwRarea:display") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                RoutingArea entity = findRoutingAreaByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = routingAreaToJSON(entity);
                em.close();
                return ret;
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
    }

    @GET
    @Path("/create")
    public Response createRoutingArea(@QueryParam("name") String name, @QueryParam("description") String description,
                                      @QueryParam("type") String type, @QueryParam("multicast") String multicast) {
        if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:create") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                if (RoutingArea.isValidType(type)) {
                    if (RoutingArea.isValidMulticastFlag(multicast)) {
                        em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                        RoutingArea entity = findRoutingAreaByName(em, name);
                        if (entity == null) {
                            entity = new RoutingArea().setNameR(name).setDescriptionR(description).setTypeR(type).setMulticastR(multicast);
                            try {
                                em.getTransaction().begin();
                                em.persist(entity);
                                em.getTransaction().commit();
                            } catch (Throwable t) {
                                if (em.getTransaction().isActive())
                                    em.getTransaction().rollback();
                                em.close();
                                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating routing area " + entity.getName() + " : " + t.getMessage()).build();
                            }
                        }
                        Response ret = routingAreaToJSON(entity);
                        em.close();
                        return ret;
                    } else
                        return Response.status(Status.BAD_REQUEST).entity("Invalid multicast flag. Correct multicast flags values are : " + RoutingArea.getMulticastFlagList().toString()).build();
                } else
                    return Response.status(Status.BAD_REQUEST).entity("Invalid type. Correct type values are : " + RoutingArea.getTypeList().toString()).build();
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name is not defined. You must define this parameter.").build();
    }

    @GET
    @Path("/delete")
    public Response deleteRoutingArea(@QueryParam("id") Long id) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete routing area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:delete") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                RoutingArea entity = findRoutingAreaById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        for (Location location : entity.getLocations())
                            location.getRoutingAreas().remove(entity);
                        for (Subnet subnet : entity.getSubnets())
                            subnet.setRarea(null);
                        em.remove(entity);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Routing area " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting routing area " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
    }

    @POST
    public Response postRoutingarea(@QueryParam("payload") String payload) throws IOException {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update routing area : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiNtwRarea:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyRoutingArea jsonFriendlyRoutingArea  = RoutingAreaJSON.JSON2Routingarea(payload);
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyRoutingArea);
            RoutingArea entity = (RoutingArea) commonRestResponse.getDeserializedObject();
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
                    Response ret = routingAreaToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating routing area " + payload + " : " + t.getMessage()).build();
                }
            } else{
                em.close();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create routing areas. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateRoutingAreaName(@QueryParam("id") Long id, @QueryParam("name") String name) {
        if (id != 0 && name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update routing area {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                RoutingArea entity = findRoutingAreaById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Routing area " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating routing area " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
    }

    @GET
    @Path("/update/description")
    public Response updateRoutingAreaDescription(@QueryParam("id") Long id, @QueryParam("description") String description) {
        if (id != 0 && description != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update routing area {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                RoutingArea entity = findRoutingAreaById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescription(description);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Routing area " + id + " has been successfully updated with description " + description).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating routing area " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
    }

    @GET
    @Path("/update/type")
    public Response updateRoutingAreaType(@QueryParam("id") Long id, @QueryParam("type") String type) {
        if (id != 0 && type != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update routing area {} type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, type});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                if (RoutingArea.isValidType(type)) {
                    em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                    RoutingArea entity = findRoutingAreaById(em, id);
                    if (entity != null) {
                        try {
                            em.getTransaction().begin();
                            entity.setType(type);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Routing area " + id + " has been successfully updated with type " + type).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating routing area " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).build();
                    }
                } else
                    return Response.status(Status.BAD_REQUEST).entity("Invalid type. Correct type values are: " + RoutingArea.getTypeList().toString()).build();
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or type are not defined. You must define these parameters.").build();
    }

    @GET
    @Path("/update/multicast")
    public Response updateRoutingAreaMulticast(@QueryParam("id") Long id, @QueryParam("multicast") String multicast) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update routing area {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, multicast});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                if (RoutingArea.isValidMulticastFlag(multicast)) {
                    em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                    RoutingArea entity = findRoutingAreaById(em, id);
                    if (entity != null) {
                        try {
                            em.getTransaction().begin();
                            entity.setMulticast(multicast);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Routing area " + id + " has been successfully updated with multicast " + multicast).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating routing area " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).build();
                    }
                } else
                    return Response.status(Status.BAD_REQUEST).entity("Invalid multicast flag. Correct multicast flags values are : " + RoutingArea.getMulticastFlagList().toString()).build();
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or boolean are not defined. You must define these parameters.").build();
    }

    @GET
    @Path("/update/locations/add")
    public Response updateRoutingAreaAddLocation(@QueryParam("id") Long id, @QueryParam("locationID") Long locID) {
        if (id != 0 && locID != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update routing area {} by adding location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, locID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                RoutingArea entity = findRoutingAreaById(em, id);
                if (entity != null) {
                    Location location = LocationEndpoint.findLocationById(em, locID);
                    if (location != null) {
                        try {
                            em.getTransaction().begin();
                            location.getRoutingAreas().add(entity);
                            entity.getLocations().add(location);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Routing area " + id + " has been successfully updated by adding location " + locID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Location " + locID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Routing area " + id + " not found.").build();
                }
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or locID are not defined. You must define these parameters.").build();
    }

    @GET
    @Path("/update/locations/delete")
    public Response updateRoutingAreaDeleteLocation(@QueryParam("id") Long id, @QueryParam("locationID") Long locID) {
        if (id != 0 && locID != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update routing area {} by deleting location : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, locID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwRarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                RoutingArea entity = findRoutingAreaById(em, id);
                if (entity != null) {
                    Location location = LocationEndpoint.findLocationById(em, locID);
                    if (location != null) {
                        try {
                            em.getTransaction().begin();
                            location.getRoutingAreas().remove(entity);
                            entity.getLocations().remove(location);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Routing area " + id + " has been successfully updated by deleting location " + locID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating location " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Location " + locID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Routing area " + id + " not found.").build();
                }
            } else
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update routing areas. Contact your administrator.").build();
        } else
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or locID are not defined. You must define these parameters.").build();
    }
}