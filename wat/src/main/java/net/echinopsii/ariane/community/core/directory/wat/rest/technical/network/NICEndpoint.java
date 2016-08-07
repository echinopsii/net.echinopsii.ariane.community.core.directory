/**
 * Directory wat
 * NIC REST endpoint
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
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.NICJSON;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NIC;
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

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.NICJSON.JSONFriendlyNIC;

@SuppressWarnings("ALL")
@Path("/directories/common/infrastructure/network/nic")
public class NICEndpoint {
    private static final Logger log = LoggerFactory.getLogger(NICEndpoint.class);
    private EntityManager em;

    public static Response nicToJSON(NIC entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            NICJSON.oneNIC2JSON(entity, outStream);
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

    public static NIC findNICById(EntityManager em, long id) {
        TypedQuery<NIC> findByIdQuery;
        findByIdQuery = em.createQuery("SELECT DISTINCT n FROM NIC n LEFT JOIN FETCH n.osInstance LEFT JOIN FETCH n.ipAddress WHERE n.id = :entityId ORDER BY n.id", NIC.class);
        findByIdQuery.setParameter("entityId", id);
        NIC entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static NIC findNICByMacAddress(EntityManager em, String macAddress) {
        TypedQuery<NIC> findByMacAddressQuery = em.createQuery("SELECT DISTINCT n FROM NIC n LEFT JOIN FETCH n.osInstance LEFT JOIN FETCH n.ipAddress WHERE n.macAddress = :entityMADDR ORDER BY n.macAddress", NIC.class);
        findByMacAddressQuery.setParameter("entityMADDR", macAddress);
        NIC entity;
        try {
            entity = findByMacAddressQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static NIC findNICByName(EntityManager em, String name) {
        TypedQuery<NIC> findByNameQuery = em.createQuery("SELECT DISTINCT n FROM NIC n LEFT JOIN FETCH n.osInstance LEFT JOIN FETCH n.ipAddress WHERE n.name = :entityNAME ORDER BY n.name", NIC.class);
        findByNameQuery.setParameter("entityNAME", name);
        NIC entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyNIC jsonFriendlyNIC) {
        NIC entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if (jsonFriendlyNIC.getNicID() != 0)
            entity = findNICById(em, jsonFriendlyNIC.getNicID());
        if (entity == null && jsonFriendlyNIC.getNicID() != 0) {
            commonRestResponse.setErrorMessage("Request Error : provided NIC ID " + jsonFriendlyNIC.getNicID() + " was not found.");
            return commonRestResponse;
        }
        if (entity == null) {
            if (jsonFriendlyNIC.getNicName() != null) {
                entity = findNICByName(em, jsonFriendlyNIC.getNicName());
            }
        }
        if (entity != null) {
            if (jsonFriendlyNIC.getNicName() != null) {
                entity.setName(jsonFriendlyNIC.getNicName());
            }
            if (jsonFriendlyNIC.getNicMacAddress() != null) {
                entity.setMacAddress(jsonFriendlyNIC.getNicMacAddress());
            }
            if (jsonFriendlyNIC.getNicDuplex() != null) {
                entity.setDuplex(jsonFriendlyNIC.getNicDuplex());
            }
            if (jsonFriendlyNIC.getNicSpeed() != -1) {
                entity.setSpeed(jsonFriendlyNIC.getNicSpeed());
            }
            if (jsonFriendlyNIC.getNicMtu() != -1) {
                entity.setMtu(jsonFriendlyNIC.getNicMtu());
            }
            if (jsonFriendlyNIC.getNicOSInstanceID() != 0) {
                OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, jsonFriendlyNIC.getNicOSInstanceID());
                if (osInstance != null) {
                    if (entity.getOsInstance() != null)
                        entity.getOsInstance().getNics().remove(entity);
                    entity.setOsInstance(osInstance);
                    osInstance.getNics().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create NIC. Reason : provided OS Instance ID " + jsonFriendlyNIC.getNicOSInstanceID() + " was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyNIC.getNicIPAddressID() != 0) {
                IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, jsonFriendlyNIC.getNicIPAddressID());
                if (ipAddress != null) {
                    if (entity.getIpAddress() != null)
                        entity.getIpAddress().setNic(null);
                    entity.setIpAddress(ipAddress);
                    ipAddress.setNic(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to update NIC. Reason : provided IPAddress ID " + jsonFriendlyNIC.getNicIPAddressID() + " was not found.");
                    return commonRestResponse;
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new NIC();
            entity.setNameR(jsonFriendlyNIC.getNicName()).setMacAddressR(jsonFriendlyNIC.getNicMacAddress())
            .setDuplexR(jsonFriendlyNIC.getNicDuplex()).setMtuR(jsonFriendlyNIC.getNicMtu()).setSpeedR(jsonFriendlyNIC.getNicSpeed());
            if (jsonFriendlyNIC.getNicOSInstanceID() != 0) {
                OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, jsonFriendlyNIC.getNicOSInstanceID());
                if (osInstance != null) {
                    if (entity.getOsInstance() != null)
                        entity.getOsInstance().getNics().remove(entity);
                    entity.setOsInstance(osInstance);
                    osInstance.getNics().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create NIC. Reason : provided OS Instance ID " + jsonFriendlyNIC.getNicOSInstanceID() + " was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyNIC.getNicIPAddressID() != 0) {
                IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, jsonFriendlyNIC.getNicIPAddressID());
                if (ipAddress != null) {
                    if (entity.getIpAddress() != null)
                        entity.getIpAddress().setNic(null);
                    entity.setIpAddress(ipAddress);
                    ipAddress.setNic(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to update NIC. Reason : provided IPAddress ID " + jsonFriendlyNIC.getNicIPAddressID() + " was not found.");
                    return commonRestResponse;
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        }
        return commonRestResponse;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayNIC(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get NICs : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwNIC:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            NIC entity = findNICById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = nicToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display NICs. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllNIC() {
        Subject subject = SecurityUtils.getSubject();
        System.out.print("in display");
        log.debug("[{}-{}] get NICs", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwNIC:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<NIC> results = new HashSet(em.createQuery("SELECT DISTINCT l FROM NIC l LEFT JOIN FETCH l.osInstance LEFT JOIN FETCH l.ipAddress ORDER BY l.id", NIC.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                NICJSON.manyNICs2JSON(results, outStream);
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
    public Response getNIC(@QueryParam("id") long id, @QueryParam("macAddress") String macAddress, @QueryParam("name") String name) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get NIC : {}", new Object[]{Thread.currentThread().getId()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwNIC:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (id != 0) {
                return displayNIC(id);
            } else if (macAddress != null || name != null) {
                Response ret;
                NIC entity;
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                if (macAddress != null) entity = findNICByMacAddress(em, macAddress);
                else entity = findNICByName(em, name);
                em.close();
                if (entity == null) ret = Response.status(Status.NOT_FOUND).build();
                else ret = nicToJSON(entity);
                return ret;
            } else return Response.status(Status.BAD_REQUEST).entity("Bad request. You should provide at least: NIC id or NIC mac address or NIC name.").build();
        } else return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display NIC. Contact your administrator.").build();
    }

    @GET
    @Path("/create")
    public Response createNIC(@QueryParam("name") String name, @QueryParam("macAddress") String macAddress) {
        if (macAddress != null && name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create NIC : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, macAddress});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:create") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICByName(em, name);
                if (entity == null) {
                    entity = new NIC().setNameR(name).setMacAddressR(macAddress);
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
                Response ret = nicToJSON(entity);
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
    public Response postNIC(@QueryParam("payload") String payload) {

        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update NIC : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiNtwNIC:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyNIC jsonFriendlyNIC = null;
            try {
                jsonFriendlyNIC = NICJSON.JSON2NIC(payload);
            } catch (IOException e) {
                log.error("Problem while deserializing payload : " + payload);
                e.printStackTrace();
                return Response.status(Status.BAD_REQUEST).entity("Problem while deserializing payload : " + payload).build();
            }
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyNIC);
            NIC entity = (NIC) commonRestResponse.getDeserializedObject();
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
                    Response ret = nicToJSON(entity);
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
    public Response deleteNIC(@QueryParam("id") Long id) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete NIC : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:delete") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        if (entity.getOsInstance() != null) {
                            entity.getOsInstance().getNics().remove(entity);
                            if (entity.getIpAddress()!=null) {
                                entity.getOsInstance().getIpAddresses().remove(entity.getIpAddress());
                                entity.getIpAddress().setOsInstance(null);
                            }
                            entity.setOsInstance(null);
                        }
                        if (entity.getIpAddress() != null) {
                            entity.getIpAddress().setNic(null);
                            entity.setIpAddressR(null);
                        }
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("NIC " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        t.printStackTrace();
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
    public Response updateNICName(@QueryParam("id") Long id, @QueryParam("name") String name) {
        if (id != 0 && name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
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
    public Response updateNICMacAddress(@QueryParam("id") Long id, @QueryParam("macAddress") String macAddress) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} macAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, macAddress});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
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
    public Response updateNICDuplex(@QueryParam("id") Long id, @QueryParam("duplex") String duplex) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} duplex : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, duplex});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
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
    public Response updateNICSpeed(@QueryParam("id") Long id, @QueryParam("speed") int speed) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} speed : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, speed});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
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
    public Response updateNICMtu(@QueryParam("id") Long id, @QueryParam("mtu") int mtu) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} mtu : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, mtu});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
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
    public Response updateNICIPAddress(@QueryParam("id") Long id, @QueryParam("ipAddressID") Long ipAddressID) {
        if (id != 0 && ipAddressID != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} ipAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ipAddressID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
                if (entity != null) {
                    IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, ipAddressID);
                    if (ipAddress != null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getIpAddress() != null)
                                entity.getIpAddress().setNic(null);
                            ipAddress.setNic(entity);
                            entity.setIpAddress(ipAddress);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("NIC " + id + " has been successfully updated with ipAddress " + ipAddressID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating NIC " + entity.getIpAddress() + " : " + t.getMessage()).build();
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
    public Response updateNICOSInstance(@QueryParam("id") Long id, @QueryParam("osInstanceID") Long osInstanceID) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update NIC {} osInstance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osInstanceID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwNIC:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                NIC entity = findNICById(em, id);
                if (entity != null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osInstanceID);
                    if (osInstance != null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getOsInstance() != null)
                                entity.getOsInstance().getNics().remove(entity);
                            osInstance.getNics().add(entity);
                            entity.setOsInstance(osInstance);
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