/**
 * Directory wat
 * NICard REST endpoint
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
package net.echinopsii.ariane.community.core.directory.wat.rest.technical.network;

import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.NICardJSON;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NICard;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
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
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.NICardJSON.JSONFriendlyNICard;

@SuppressWarnings("ALL")
@Path("/directories/common/infrastructure/network/niCard")
public class NICardEndpoint {
    private static final Logger log = LoggerFactory.getLogger(RoutingAreaEndpoint.class);
    private EntityManager em;

    public static Response niCardToJSON(NICard entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            NICardJSON.oneNICard2JSON(entity, outStream);
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

    public static NICard findNICardById(EntityManager em, long id) {
        TypedQuery<NICard> findByIdQuery;
        findByIdQuery = em.createQuery("SELECT DISTINCT l FROM NICard l LEFT JOIN FETCH l.rosInstance LEFT JOIN FETCH l.ripAddress WHERE l.id = :entityId ORDER BY l.id", NICard.class);
        findByIdQuery.setParameter("entityId", id);
        NICard entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static NICard findNICardByName(EntityManager em, String name) {
        TypedQuery<NICard> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM NICard l LEFT JOIN FETCH l.rosInstance LEFT JOIN FETCH l.ripAddress WHERE l.name = :entityNAME ORDER BY l.macAddress", NICard.class);
        findByNameQuery.setParameter("entityNAME", name);
        NICard entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyNICard jsonFriendlyNICard) {
        NICard entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if (jsonFriendlyNICard.getNiCardID() != 0)
            entity = findNICardById(em, jsonFriendlyNICard.getNiCardID());
        if (entity == null && jsonFriendlyNICard.getNiCardID() != 0) {
            commonRestResponse.setErrorMessage("Request Error : provided NICard ID " + jsonFriendlyNICard.getNiCardID() + " was not found.");
            return commonRestResponse;
        }
        if (entity == null) {
            if (jsonFriendlyNICard.getNiCardName() != null) {
                entity = findNICardByName(em, jsonFriendlyNICard.getNiCardName());
            }
        }
        if (entity != null) {
            if (jsonFriendlyNICard.getNiCardName() != null) {
                entity.setName(jsonFriendlyNICard.getNiCardName());
            }
            if (jsonFriendlyNICard.getNiCardMacAddress() != null) {
                entity.setMacAddress(jsonFriendlyNICard.getNiCardMacAddress());
            }
            if (jsonFriendlyNICard.getNiCardDuplex() != null) {
                entity.setDuplex(jsonFriendlyNICard.getNiCardDuplex());
            }
            if (jsonFriendlyNICard.getNiCardSpeed() != -1) {
                entity.setSpeed(jsonFriendlyNICard.getNiCardSpeed());
            }
            if (jsonFriendlyNICard.getNiCardMtu() != -1) {
                entity.setMtu(jsonFriendlyNICard.getNiCardMtu());
            }
            if (jsonFriendlyNICard.getNiCardOSInstanceID() != 0) {
                OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, jsonFriendlyNICard.getNiCardOSInstanceID());
                if (osInstance != null) {
                    if (entity.getRosInstance() != null)
                        entity.getRosInstance().getNiCards().remove(entity);
                    entity.setRosInstance(osInstance);
                    osInstance.getNiCards().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create NIC. Reason : provided OS Instance ID " + jsonFriendlyNICard.getNiCardOSInstanceID() + " was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyNICard.getNiCardIPAddressID() != 0) {
                IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, jsonFriendlyNICard.getNiCardIPAddressID());
                if (ipAddress != null) {
                    if (entity.getRipAddress() != null)
                        entity.getRipAddress().setNiCard(null);
                    entity.setRipAddress(ipAddress);
                    ipAddress.setNiCard(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to update NIC. Reason : provided IPAddress ID " + jsonFriendlyNICard.getNiCardIPAddressID() + " was not found.");
                    return commonRestResponse;
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new NICard();
            entity.setNameR(jsonFriendlyNICard.getNiCardName()).setMacAddressR(jsonFriendlyNICard.getNiCardMacAddress())
            .setDuplexR(jsonFriendlyNICard.getNiCardDuplex()).setMtuR(jsonFriendlyNICard.getNiCardMtu()).setSpeedR(jsonFriendlyNICard.getNiCardSpeed());
            if (jsonFriendlyNICard.getNiCardOSInstanceID() != 0) {
                OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, jsonFriendlyNICard.getNiCardOSInstanceID());
                if (osInstance != null) {
                    if (entity.getRosInstance() != null)
                        entity.getRosInstance().getNiCards().remove(entity);
                    entity.setRosInstance(osInstance);
                    osInstance.getNiCards().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create NIC. Reason : provided OS Instance ID " + jsonFriendlyNICard.getNiCardOSInstanceID() + " was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyNICard.getNiCardIPAddressID() != 0) {
                IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, jsonFriendlyNICard.getNiCardIPAddressID());
                if (ipAddress != null) {
                    if (entity.getRipAddress() != null)
                        entity.getRipAddress().setNiCard(null);
                    entity.setRipAddress(ipAddress);
                    ipAddress.setNiCard(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to update NIC. Reason : provided IPAddress ID " + jsonFriendlyNICard.getNiCardIPAddressID() + " was not found.");
                    return commonRestResponse;
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        }
        return commonRestResponse;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayNICard(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get NICs : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwNICard:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            NICard entity = findNICardById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = niCardToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display NICs. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllNICard() {
        Subject subject = SecurityUtils.getSubject();
        System.out.print("in display");
        log.debug("[{}-{}] get niCards", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwNICard:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<NICard> results = new HashSet(em.createQuery("SELECT DISTINCT l FROM NICard l LEFT JOIN FETCH l.rosInstance LEFT JOIN FETCH l.ripAddress ORDER BY l.id", NICard.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                NICardJSON.manyNICards2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display NIC. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getNICard(@QueryParam("name") String name, @QueryParam("id") long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get niCard : {}", new Object[]{Thread.currentThread().getId()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwNICard:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (id != 0) {
                return displayNICard(id);
            } else if (name != null) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Response ret;
                NICard entity = findNICardByName(em, name);
                if (entity == null)
                    ret = Response.status(Status.NOT_FOUND).build();
                else
                    ret = niCardToJSON(entity);
                em.close();
                return ret;
            } else
                return Response.status(Status.BAD_REQUEST).entity("Bad request. You should provide at least: NIC id or NIC name.").build();
        } else
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display NIC. Contact your administrator.").build();
    }

    @GET
    @Path("/create")
    public Response createNICard(@QueryParam("name") String name, @QueryParam("macAddress") String macAddress) {
        if (macAddress != null && name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create niCard : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, macAddress});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:create") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardByName(em, name);
                if (entity == null) {
                    entity = new NICard().setNameR(name).setMacAddressR(macAddress);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating NIC" + entity.getName() + " : " + t.getMessage()).build();
                    }
                }
                Response ret = niCardToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create NIC. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or macAddress are not defined. " +
                    "You must define these parameters.").build();
        }
    }

    @POST
    public Response postNICard(@QueryParam("payload") String payload) throws IOException {

        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update NIC : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiNtwNICard:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyNICard jsonFriendlyNICard = NICardJSON.JSON2NICard(payload);
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyNICard);
            NICard entity = (NICard) commonRestResponse.getDeserializedObject();
            if (entity != null) {
                try {
                    em.getTransaction().begin();
                    if (entity.getId() == null) {
                        em.persist(entity);
                        em.flush();
                        em.getTransaction().commit();
                    } else {
                        em.merge(entity);
                        em.flush();
                        em.getTransaction().commit();
                    }
                    Response ret = niCardToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating NIC " + payload + " : " + t.getMessage()).build();
                }
            } else {
                em.close();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create NICs. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteNICard(@QueryParam("id") Long id) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete niCard : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:delete") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        if (entity.getRosInstance() != null)
                            entity.getRosInstance().getNiCards().remove(entity);
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("NIC " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating NIC " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete NIC. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is no defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateNICardName(@QueryParam("id") Long id, @QueryParam("name") String name) {
        if (id != 0 && name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating name " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update NIC. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/macAddress")
    public Response updateNICardMacAddress(@QueryParam("id") Long id, @QueryParam("macAddress") String macAddress) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} macAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, macAddress});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setMacAddress(macAddress);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with MacAddress " + macAddress).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating MacAddress " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update MacAddress. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/duplex")
    public Response updateNICardDuplex(@QueryParam("id") Long id, @QueryParam("duplex") String duplex) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} duplex : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, duplex});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDuplex(duplex);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with Duplex " + duplex).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating Duplex " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update Duplex. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/speed")
    public Response updateNICardSpeed(@QueryParam("id") Long id, @QueryParam("speed") int speed) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} speed : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, speed});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setSpeed(speed);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with Speed " + speed).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating Speed " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update Speed. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/mtu")
    public Response updateNICardMtu(@QueryParam("id") Long id, @QueryParam("mtu") int mtu) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} mtu : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, mtu});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setMtu(mtu);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with MTU " + mtu).build();
                    } catch (Throwable t) {
                        if (em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating MTU " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update MTU. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/ipAddress")
    public Response updateNICardIPAddres(@QueryParam("id") Long id, @QueryParam("ipAddressID") Long ipAddressID) {
        if (id != 0 && ipAddressID != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NICard {} ipAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ipAddressID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, ipAddressID);
                    if (ipAddress != null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getRipAddress() != null)
                                entity.getRipAddress().setNiCard(null);
                            ipAddress.setNiCard(entity);
                            entity.setRipAddress(ipAddress);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with ipAddress " + ipAddressID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating NIC " + entity.getRipAddress() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("IPAddress " + ipAddressID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("NIC " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update NIC. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or ipAddress are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osInstance")
    public Response updateNICardOSInstance(@QueryParam("id") Long id, @QueryParam("osInstanceID") Long osInstanceID) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} osInstance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osInstanceID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNICard:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NICard entity = findNICardById(em, id);
                if (entity != null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osInstanceID);
                    if (osInstance != null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getRosInstance() != null)
                                entity.getRosInstance().getNiCards().remove(entity);
                            osInstance.getNiCards().add(entity);
                            entity.setRosInstance(osInstance);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with osInstnace " + osInstanceID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating NIC " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS Istance " + osInstanceID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("NIC " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update NIC. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define these parameters.").build();
        }
    }
}