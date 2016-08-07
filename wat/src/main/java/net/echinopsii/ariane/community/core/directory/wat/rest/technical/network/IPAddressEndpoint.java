/**
 * Directory wat
 * Subnet REST endpoint
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
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.IPAddressJSON;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.NIC;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
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

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.network.IPAddressJSON.JSONFriendlyIPAddress;

@SuppressWarnings("ALL")
@Path("/directories/common/infrastructure/network/ipAddress")
public class IPAddressEndpoint {
    private static final Logger log = LoggerFactory.getLogger(IPAddressEndpoint.class);
    private EntityManager em;

    public static Response ipAddressToJSON(IPAddress entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            IPAddressJSON.oneIPAddress2JSON(entity, outStream);
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

    public static IPAddress findIPAddressById(EntityManager em, long id) {
        TypedQuery<IPAddress> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM IPAddress l LEFT JOIN FETCH l.osInstance LEFT JOIN FETCH l.networkSubnet WHERE l.id = :entityId ORDER BY l.id", IPAddress.class);
        findByIdQuery.setParameter("entityId", id);
        IPAddress entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static IPAddress findIPAddressByIPAndSubnet(EntityManager em, String ipAddress, long subnetID) {
        TypedQuery<IPAddress> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM IPAddress l LEFT JOIN FETCH l.osInstance LEFT JOIN FETCH l.networkSubnet WHERE l.ipAddress = :entityIPA AND l.networkSubnet.id = :subnetID ORDER BY l.ipAddress", IPAddress.class);
        findByNameQuery.setParameter("entityIPA", ipAddress).setParameter("subnetID", subnetID);
        IPAddress entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static IPAddress findIPAddressByIPAndOSI(EntityManager em, String ipAddress, long osiID) {
        TypedQuery<IPAddress> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM IPAddress l LEFT JOIN FETCH l.osInstance LEFT JOIN FETCH l.networkSubnet WHERE l.ipAddress = :entityIPA AND l.osInstance.id = :osiID ORDER BY l.ipAddress", IPAddress.class);
        findByNameQuery.setParameter("entityIPA", ipAddress).setParameter("osiID", osiID);
        IPAddress entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static IPAddress findIPAddressByFQDN(EntityManager em, String fqdn) {
        TypedQuery<IPAddress> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM IPAddress l LEFT JOIN FETCH l.osInstance LEFT JOIN FETCH l.networkSubnet WHERE l.fqdn = :entityFQDN ORDER BY l.ipAddress", IPAddress.class);
        findByNameQuery.setParameter("entityFQDN", fqdn);
        IPAddress entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyIPAddress jsonFriendlyIPAddress) {
        IPAddress entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if(jsonFriendlyIPAddress.getIpAddressID() !=0)
            entity = findIPAddressById(em, jsonFriendlyIPAddress.getIpAddressID());
        if(entity == null && jsonFriendlyIPAddress.getIpAddressID()!=0){
            commonRestResponse.setErrorMessage("Request Error : provided IPAddress ID " + jsonFriendlyIPAddress.getIpAddressID() +" was not found.");
            return commonRestResponse;
        }
        if(entity == null){
            if(jsonFriendlyIPAddress.getIpAddressIPA() != null){
                entity = findIPAddressByFQDN(em, jsonFriendlyIPAddress.getIpAddressFQDN());
            }
        }
        if(entity != null) {
            if (jsonFriendlyIPAddress.getIpAddressIPA() !=null) {
                entity.setIpAddress(jsonFriendlyIPAddress.getIpAddressIPA());
            }
            if (jsonFriendlyIPAddress.getIpAddressFQDN() != null) {
                entity.setFqdn(jsonFriendlyIPAddress.getIpAddressFQDN());
            }
            if (jsonFriendlyIPAddress.getIpAddressOSInstanceID() != 0) {
                OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, jsonFriendlyIPAddress.getIpAddressOSInstanceID());
                if (osInstance!= null) {
                    if (entity.getOsInstance() != null)
                        entity.getOsInstance().getIpAddresses().remove(entity);
                    entity.setOsInstance(osInstance);
                    osInstance.getIpAddresses().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create IPAddress. Reason : provided OS Instance ID " + jsonFriendlyIPAddress.getIpAddressOSInstanceID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyIPAddress.getIpAddressNICID() != 0) {
                NIC nic = NICEndpoint.findNICById(em, jsonFriendlyIPAddress.getIpAddressNICID());
                if (nic!= null) {
                    if (entity.getNic() != null)
                        entity.getNic().setIpAddress(null);
                    entity.setNic(nic);
                    nic.setIpAddress(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create IPAddress. Reason : provided NIC ID " + jsonFriendlyIPAddress.getIpAddressNICID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyIPAddress.getIpAddressSubnetID() != 0) {
                Subnet subnet = SubnetEndpoint.findSubnetById(em, jsonFriendlyIPAddress.getIpAddressSubnetID());
                if (subnet != null) {
                    if (entity.getNetworkSubnet() != null)
                        entity.getNetworkSubnet().getIpAddresses().remove(entity);
                    entity.setNetworkSubnet(subnet);
                    subnet.getIpAddresses().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to update IPAddress. Reason : provided Subnet ID " + jsonFriendlyIPAddress.getIpAddressSubnetID() + " was not found.");
                    return commonRestResponse;
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new IPAddress();
            entity.setIpAddressR(jsonFriendlyIPAddress.getIpAddressIPA()).setFqdnR(jsonFriendlyIPAddress.getIpAddressFQDN());
            if (jsonFriendlyIPAddress.getIpAddressOSInstanceID() != 0) {
                OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, jsonFriendlyIPAddress.getIpAddressOSInstanceID());
                if (osInstance!= null) {
                    if (entity.getOsInstance() != null)
                        entity.getOsInstance().getIpAddresses().remove(entity);
                    entity.setOsInstance(osInstance);
                    osInstance.getIpAddresses().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create IPAddress. Reason : provided OS Instance ID " + jsonFriendlyIPAddress.getIpAddressOSInstanceID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyIPAddress.getIpAddressNICID() != 0) {
                NIC nic = NICEndpoint.findNICById(em, jsonFriendlyIPAddress.getIpAddressNICID());
                if (nic!= null) {
                    if (entity.getNic() != null)
                        entity.getNic().setIpAddress(null);
                    entity.setNic(nic);
                    nic.setIpAddress(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create IPAddress. Reason : provided NIC ID " + jsonFriendlyIPAddress.getIpAddressNICID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if (jsonFriendlyIPAddress.getIpAddressSubnetID() != 0) {
                Subnet subnet = SubnetEndpoint.findSubnetById(em, jsonFriendlyIPAddress.getIpAddressSubnetID());
                if (subnet != null) {
                    if (entity.getNetworkSubnet() != null)
                        entity.getNetworkSubnet().getIpAddresses().remove(entity);
                    entity.setNetworkSubnet(subnet);
                    subnet.getIpAddresses().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to update IPAddress. Reason : provided Subnet ID " + jsonFriendlyIPAddress.getIpAddressSubnetID() + " was not found.");
                    return commonRestResponse;
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        }
        return commonRestResponse;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayIPAddress(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get ipAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwIPAddress:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            IPAddress entity = findIPAddressById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = ipAddressToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display ipAddress. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllIPAddress() {
        Subject subject = SecurityUtils.getSubject();
        System.out.print("in display");
        log.debug("[{}-{}] get ipAddresses", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwIPAddress:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<IPAddress> results = new HashSet(em.createQuery("SELECT DISTINCT l FROM IPAddress l LEFT JOIN FETCH l.osInstance LEFT JOIN FETCH l.networkSubnet ORDER BY l.id", IPAddress.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                IPAddressJSON.manyIPAddresses2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display ipAddress. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getIPAddress(@QueryParam("ipAddress")String ipAddress, @QueryParam("subnetID") long subnetID, @QueryParam("osiID") long osiID, @QueryParam("fqdn") String fqdn, @QueryParam("id")long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), ipAddress});
        if (subject.hasRole("ntwadmin") || subject.hasRole("ntwreviewer") || subject.isPermitted("dirComITiNtwIPAddress:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            if (id != 0) {
                return displayIPAddress(id);
            } else if (fqdn != null) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Response ret ;
                IPAddress entity = findIPAddressByFQDN(em, fqdn);
                if (entity == null)
                    ret = Response.status(Status.NOT_FOUND).build();
                else
                    ret = ipAddressToJSON(entity);
                em.close();
                return ret;
            } else if (ipAddress != null && subnetID != 0) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Response ret ;
                IPAddress entity = findIPAddressByIPAndSubnet(em, ipAddress, subnetID);
                if (entity == null)
                    ret = Response.status(Status.NOT_FOUND).build();
                else
                    ret = ipAddressToJSON(entity);
                em.close();
                return ret;
            } else if (ipAddress != null && osiID != 0) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Response ret ;
                IPAddress entity = findIPAddressByIPAndOSI(em, ipAddress, osiID);
                if (entity == null)
                    ret = Response.status(Status.NOT_FOUND).build();
                else
                    ret = ipAddressToJSON(entity);
                em.close();
                return ret;
            } else
                return Response.status(Status.BAD_REQUEST).entity("Bad request. You should provide at least: IP id or IP fqdn or (IP address and Subnet ID) or (IP address or OS instance ID).").build();
        } else
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display ipAddress. Contact your administrator.").build();
    }

    @GET
    @Path("/create")
    public Response createIPAddress(@QueryParam("ipAddress")String ipAddress, @QueryParam("fqdn")String fqdn,
                                    @QueryParam("networkSubnet")long subnetID, @QueryParam("osInstance")int osInstanceID) {
        if (ipAddress!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create ipAddress : ({},{},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), ipAddress, fqdn, subnetID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwIPAddress:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Response ret;
                Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                if (subnet!=null) {
                    IPAddress entity = findIPAddressByFQDN(em, fqdn);
                    if (entity==null) {
                        OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osInstanceID);
                        entity = new IPAddress().setIpAddressR(ipAddress).setFqdnR(fqdn).setNetworkSubnetR(subnet).setOsInstancesR(osInstance);
                        try {
                            em.getTransaction().begin();
                            em.persist(entity);
                            em.getTransaction().commit();
                            ret = ipAddressToJSON(entity);
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            ret = Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating IPAddress " + entity.getIpAddress() + " : " + t.getMessage()).build();
                        }
                    } else
                        ret = ipAddressToJSON(entity);
                } else
                    ret = Response.status(Status.BAD_REQUEST).entity("Wrong subnet " + subnetID + ".").build();
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create IPAddress. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: ipAddress and/or FQDN are not defined. " +
                    "You must define these parameters.").build();
        }
    }

    @POST
    public Response postIPAddress(@QueryParam("payload") String payload) {

        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update IPAddress : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiNtwIPAddress:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyIPAddress jsonFriendlyIPAddress  = null;
            try {
                jsonFriendlyIPAddress = IPAddressJSON.JSON2IPAddress(payload);
            } catch (IOException e) {
                log.error("Problem while deserializing payload : " + payload);
                e.printStackTrace();
                return Response.status(Status.BAD_REQUEST).entity("Problem while deserializing payload : " + payload).build();
            }
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyIPAddress);
            IPAddress entity = (IPAddress) commonRestResponse.getDeserializedObject();
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
                    Response ret = ipAddressToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating ipAddress " + payload + " : " + t.getMessage()).build();
                }
            } else{
                em.close();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create ipAddresses. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteIPAddress(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete ipAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwIPAddress:delete") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                IPAddress entity = findIPAddressById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        if (entity.getNetworkSubnet()!=null)
                            entity.getNetworkSubnet().getIpAddresses().remove(entity);
                        if (entity.getNic()!=null)
                            entity.getNic().setIpAddress(null);
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("IPAddress " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating ipAddress " + entity.getIpAddress() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete ipAddress. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is no defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/ipAddress")
    public Response updateIPAddressIPA(@QueryParam("id")Long id, @QueryParam("ipAddress")String ipAddress) {
        if (id!=0 && ipAddress!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update IPAddress {} ipAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ipAddress});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwIPAddress:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                IPAddress entity = findIPAddressById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setIpAddress(ipAddress);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("IPAddress " + id + " has been successfully updated with ipAddress " + ipAddress).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating ipAddress " + entity.getIpAddress() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update ipAddress. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or ipAddress are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/fqdn")
    public Response updateIPAddressFQDN(@QueryParam("id")Long id, @QueryParam("fqdn")String fqdn) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update IPAddress {} fqdn : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, fqdn});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwIPAddress:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                IPAddress entity = findIPAddressById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setFqdn(fqdn);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("IPAddress " + id + " has been successfully updated with FQDN " + fqdn).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating FQDN " + entity.getFqdn() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update FQDN. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnet")
    public Response updateIPAddressSubnet(@QueryParam("id") Long id, @QueryParam("subnetID") Long subnetID) {
        if (id!=0 && subnetID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update ipAddress {} subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwIPAddress:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                IPAddress entity = findIPAddressById(em, id);
                if (entity!=null) {
                    Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                    if (subnet!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getNetworkSubnet()!=null)
                                entity.getNetworkSubnet().getIpAddresses().remove(entity);
                            subnet.getIpAddresses().add(entity);
                            entity.setNetworkSubnet(subnet);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("IPAddress " + id + " has been successfully updated with subnet " + subnetID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating IPAddress " + entity.getIpAddress() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Subnet " + subnetID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("IPAddress " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update ipAddress. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnet are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osInstance")
    public Response updateIPAddressOSInstance(@QueryParam("id") Long id, @QueryParam("osInstanceID") Long osInstanceID) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update ipAddress {} osInstance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osInstanceID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwIPAddress:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                IPAddress entity = findIPAddressById(em, id);
                if (entity!=null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osInstanceID);
                    if (osInstance!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getOsInstance()!=null)
                                entity.getOsInstance().getIpAddresses().remove(entity);
                            osInstance.getIpAddresses().add(entity);
                            entity.setOsInstance(osInstance);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("IPAddress " + id + " has been successfully updated with osInstnace " + osInstanceID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating IPAddress " + entity.getIpAddress() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS Istance " + osInstanceID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("IPAddress " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update ipAddress. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/nic")
    public Response updateIPAddressNIC(@QueryParam("id") Long id, @QueryParam("nicID") Long nicID) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update ipAddress {} nicID : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, nicID});
            if (subject.hasRole("ntwadmin") || subject.isPermitted("dirComITiNtwIPAddress:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                IPAddress entity = findIPAddressById(em, id);
                if (entity!=null) {
                    NIC nic = NICEndpoint.findNICById(em, nicID);
                    if (nic!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getNic()!=null)
                                entity.getNic().setIpAddress(null);
                            nic.setIpAddressR(entity);
                            entity.setNic(nic);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("IPAddress " + id + " has been successfully updated with NIC " + nicID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating IPAddress " + entity.getIpAddress() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("NIC " + nicID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("IPAddress " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update ipAddress. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id or nicID is not defined. You must define these parameters.").build();
        }
    }
}