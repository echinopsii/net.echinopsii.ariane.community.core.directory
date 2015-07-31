/**
 * Directory wat
 * OS Instance REST endpoint
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
package net.echinopsii.ariane.community.core.directory.wat.rest.technical.system;

import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.IPAddress;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSType;
import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.system.OSInstanceJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.rest.organisational.ApplicationEndpoint;
import net.echinopsii.ariane.community.core.directory.wat.rest.organisational.EnvironmentEndpoint;
import net.echinopsii.ariane.community.core.directory.wat.rest.organisational.TeamEndpoint;
import net.echinopsii.ariane.community.core.directory.wat.rest.technical.network.IPAddressEndpoint;
import net.echinopsii.ariane.community.core.directory.wat.rest.technical.network.SubnetEndpoint;
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
import java.util.HashSet;

/**
 *
 */
@Path("/directories/common/infrastructure/system/osinstances")
public class OSInstanceEndpoint {
    private static final Logger log = LoggerFactory.getLogger(OSInstanceEndpoint.class);
    private EntityManager em;

    public static Response osInstanceToJSON(OSInstance osInstance) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            OSInstanceJSON.oneOSInstance2JSON(osInstance, outStream);
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

    public static OSInstance findOSInstanceById(EntityManager em, long id) {
        TypedQuery<OSInstance> findByIdQuery = em.createQuery("SELECT DISTINCT o FROM OSInstance o LEFT JOIN FETCH o.networkSubnets LEFT JOIN FETCH o.embeddingOSInstance LEFT JOIN FETCH o.embeddedOSInstances LEFT JOIN FETCH o.osType LEFT JOIN FETCH o.applications LEFT JOIN FETCH o.teams LEFT JOIN FETCH o.environments WHERE o.id = :entityId ORDER BY o.id", OSInstance.class);
        findByIdQuery.setParameter("entityId", id);
        OSInstance entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static OSInstance findOSInstanceByName(EntityManager em, String name) {
        TypedQuery<OSInstance> findByNameQuery = em.createQuery("SELECT DISTINCT o FROM OSInstance o LEFT JOIN FETCH o.networkSubnets LEFT JOIN FETCH o.embeddingOSInstance LEFT JOIN FETCH o.embeddedOSInstances LEFT JOIN FETCH o.osType LEFT JOIN FETCH o.applications LEFT JOIN FETCH o.teams LEFT JOIN FETCH o.environments WHERE o.name = :entityName ORDER BY o.name", OSInstance.class);
        findByNameQuery.setParameter("entityName", name);
        OSInstance entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayOSInstance(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("sysadmin") || subject.hasRole("sysreviewer") || subject.isPermitted("dirComITiSysOsi:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            OSInstance entity = findOSInstanceById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = osInstanceToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display OS Instances. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllOSInstance() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get os instances", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("sysadmin") || subject.hasRole("sysreviewer") || subject.isPermitted("dirComITiSysOsi:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<OSInstance> results = new HashSet(em.createQuery("SELECT DISTINCT o FROM OSInstance o LEFT JOIN FETCH o.networkSubnets LEFT JOIN FETCH o.embeddingOSInstance LEFT JOIN FETCH o.embeddedOSInstances LEFT JOIN FETCH o.osType LEFT JOIN FETCH o.applications LEFT JOIN FETCH o.teams LEFT JOIN FETCH o.environments ORDER BY o.id", OSInstance.class).getResultList());
            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                OSInstanceJSON.manyOSInstances2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display OS Instances. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getOSInstance(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
            return displayOSInstance(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("sysadmin") || subject.hasRole("sysreviewer") || subject.isPermitted("dirComITiSysOsi:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = osInstanceToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createOSInstance(@QueryParam("name")String name, @QueryParam("adminGateURI")String uri, @QueryParam("description")String description) {
        if (name!=null && uri!= null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create os instance : ({},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, uri, description});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceByName(em, name);
                if (entity == null) {
                    entity = new OSInstance().setNameR(name).setAdminGateURIR(uri).setDescriptionR(description);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.flush();
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                    }
                }

                Response ret = osInstanceToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or adminGateURI are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteOSInstance(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        if (entity.getOsType()!=null)
                            entity.getOsType().getOsInstances().remove(entity);
                        if (entity.getEmbeddingOSInstance()!=null)
                            entity.getEmbeddingOSInstance().getEmbeddedOSInstances().remove(entity);
                        for (OSInstance embeddedOSI : entity.getEmbeddedOSInstances())
                            embeddedOSI.setEmbeddingOSInstance(null);
                        for (Subnet subnet : entity.getNetworkSubnets())
                            subnet.getOsInstances().remove(entity);
                        for (Application application : entity.getApplications())
                            application.getOsInstances().remove(entity);
                        for (Team team : entity.getTeams())
                            team.getOsInstances().remove(entity);
                        for (Environment environment : entity.getEnvironments())
                            environment.getOsInstances().remove(entity);
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("OS instance " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting OS instance " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateOSInstanceName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/admingateuri")
    public Response updateOSInstanceAdminGateURI(@QueryParam("id")Long id, @QueryParam("adminGateURI")String uri) {
        if (id!=0 && uri!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} uri : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, uri});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setAdminGateURI(uri);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated with admin gate uri " + uri).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or adminGateURI are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateOSInstanceDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescription(description);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated with description " + description).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/ostype")
    public Response updateOSInstanceOSType(@QueryParam("id")Long id, @QueryParam("ostID")Long ostID) {
        if (id!=0 && ostID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} OS type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ostID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    OSType osType = OSTypeEndpoint.findOSTypeById(em, ostID);
                    if (osType!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getOsType()!=null && !entity.getOsType().equals(osType)) {
                                entity.getOsType().getOsInstances().remove(entity);
                            }
                            osType.getOsInstances().add(entity);
                            entity.setOsType(osType);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated with OS type " + ostID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS type " + ostID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or ostID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/embeddingOSInstance")
    public Response updateOSInstanceEmbeddingOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} embedding os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    OSInstance embeddingOSI = findOSInstanceById(em, osiID);
                    if (embeddingOSI!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getEmbeddingOSInstance()!=null && !entity.getEmbeddedOSInstances().equals(embeddingOSI))
                                entity.getEmbeddingOSInstance().getEmbeddedOSInstances().remove(entity);
                            embeddingOSI.getEmbeddedOSInstances().add(entity);
                            entity.setEmbeddingOSInstance(embeddingOSI);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated with embedding OS instance " + osiID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Embedding OS instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or osiID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/add")
    public Response updateOSIntanceAddSubnet(@QueryParam("id")Long id, @QueryParam("subnetID")Long subnetID) {
        if (id!=0 && subnetID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by adding subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                    if (subnet!=null) {
                        try {
                            em.getTransaction().begin();
                            subnet.getOsInstances().add(entity);
                            entity.getNetworkSubnets().add(subnet);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by adding subnet " + subnetID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Subnet " + subnetID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/delete")
    public Response updateOSIntanceDeleteSubnet(@QueryParam("id")Long id, @QueryParam("subnetID")Long subnetID) {
        if (id!=0 && subnetID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by deleting subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                    if (subnet!=null) {
                        try {
                            em.getTransaction().begin();
                            subnet.getOsInstances().remove(entity);
                            entity.getNetworkSubnets().remove(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by deleting subnet " + subnetID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Subnet " + subnetID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/embeddedOSInstances/add")
    public Response updateOSIntanceAddEmbeddedOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by adding embedded os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    OSInstance embeddedOSI = findOSInstanceById(em, osiID);
                    if (embeddedOSI!=null) {
                        try {
                            em.getTransaction().begin();
                            if (embeddedOSI.getEmbeddingOSInstance()!=null && !embeddedOSI.getEmbeddingOSInstance().equals(entity))
                                embeddedOSI.getEmbeddingOSInstance().getEmbeddedOSInstances().remove(embeddedOSI);
                            embeddedOSI.setEmbeddingOSInstance(entity);
                            entity.getEmbeddedOSInstances().add(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by adding embedded OS instance " + osiID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Embedded OS instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or osiID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/embeddedOSInstances/delete")
    public Response updateOSIntanceDeleteEmbeddedOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by deleting embedded os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    OSInstance embeddedOSI = findOSInstanceById(em, osiID);
                    if (embeddedOSI!=null) {
                        try {
                            em.getTransaction().begin();
                            embeddedOSI.setEmbeddingOSInstance(null);
                            entity.getEmbeddedOSInstances().remove(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by deleting embedded OS instance " + osiID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Embedded OS instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or osiID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/applications/add")
    public Response updateOSInstanceAddApplication(@QueryParam("id")Long id, @QueryParam("applicationID")Long applicationID) {
        if (id!=0 && applicationID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by adding application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, applicationID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Application application = ApplicationEndpoint.findApplicationById(em, applicationID);
                    if (application!=null) {
                        try {
                            em.getTransaction().begin();
                            application.getOsInstances().add(entity);
                            entity.getApplications().add(application);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by adding application " + applicationID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Application " + applicationID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or applicationID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/applications/delete")
    public Response updateOSInstanceDeleteApplication(@QueryParam("id")Long id, @QueryParam("applicationID")Long applicationID) {
        if (id!=0 && applicationID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by deleting application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, applicationID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Application application = ApplicationEndpoint.findApplicationById(em, applicationID);
                    if (application!=null) {
                        try {
                            em.getTransaction().begin();
                            application.getOsInstances().remove(entity);
                            entity.getApplications().remove(application);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by deleting application " + applicationID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Application " + applicationID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or applicationID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/teams/add")
    public Response updateOSInstanceAddTeam(@QueryParam("id")Long id, @QueryParam("teamID")Long teamID) {
        if (id!=0 && teamID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by adding team : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, teamID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Team team = TeamEndpoint.findTeamById(em,teamID);
                    if (team!=null) {
                        try {
                            em.getTransaction().begin();
                            team.getOsInstances().add(entity);
                            entity.getTeams().add(team);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by adding team " + teamID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Team " + teamID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or teamID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/teams/delete")
    public Response updateOSInstanceDeleteTeam(@QueryParam("id")Long id, @QueryParam("teamID")Long teamID) {
        if (id!=0 && teamID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by deleting team : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, teamID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Team team = TeamEndpoint.findTeamById(em,teamID);
                    if (team!=null) {
                        try {
                            em.getTransaction().begin();
                            team.getOsInstances().remove(entity);
                            entity.getTeams().remove(team);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by deleting team " + teamID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Team " + teamID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or teamID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/environments/add")
    public Response updateOSInstanceAddEnvironment(@QueryParam("id")Long id, @QueryParam("environmentID")Long environmentID) {
        if (id!=0 && environmentID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by adding environment : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, environmentID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Environment environment = EnvironmentEndpoint.findEnvironmentByID(em, environmentID);
                    if (environment!=null) {
                        try {
                            em.getTransaction().begin();
                            environment.getOsInstances().add(entity);
                            entity.getEnvironments().add(environment);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by adding environment " + environmentID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Environment " + environmentID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or environmentID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/environments/delete")
    public Response updateOSInstanceDeleteEnvironment(@QueryParam("id")Long id, @QueryParam("environmentID")Long environmentID) {
        if (id!=0 && environmentID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by deleting environment : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, environmentID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    Environment environment = EnvironmentEndpoint.findEnvironmentByID(em, environmentID);
                    if (environment!=null) {
                        try {
                            em.getTransaction().begin();
                            environment.getOsInstances().remove(entity);
                            entity.getEnvironments().remove(environment);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by deleting environment " + environmentID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Environment " + environmentID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS instance " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Instances. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or environmentID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/ipAddresses/add")
    public Response updateOSInstanceAddIPAddress(@QueryParam("id")Long id, @QueryParam("ipAddressID")Long ipAddressID) {
        if (id!=0 && ipAddressID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by adding ipAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ipAddressID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    IPAddress ipAddress = IPAddressEndpoint.findIPAddressById(em, ipAddressID);
                    if (ipAddress!=null) {
                        try {
                            em.getTransaction().begin();
                            ipAddress.setOsInstance(entity);
                            entity.getIpAddresses().add(ipAddress);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS Instance " + id + " has been successfully updated by adding IP Address " + ipAddressID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating IP Address " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("IP Address " + ipAddressID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("IP Address " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update IP Address. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or ipAddressID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/ipAddresses/delete")
    public Response updateOSInstanceDeleteIPAddress(@QueryParam("id")Long id, @QueryParam("ipAddressID")Long ipAddressID) {
        if (id!=0 && ipAddressID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os instance {} by deleting ipAddress : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ipAddressID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOsi:update") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSInstance entity = findOSInstanceById(em, id);
                if (entity!=null) {
                    IPAddress ipAddress= IPAddressEndpoint.findIPAddressById(em, ipAddressID);
                    if (ipAddress!=null) {
                        try {
                            em.getTransaction().begin();
                            ipAddress.setOsInstance(null);
                            entity.getIpAddresses().remove(ipAddress);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS instance " + id + " has been successfully updated by deleting IP Address " + ipAddressID).build();
                        } catch (Throwable t) {
                            if (em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS instance " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("IP Address " + ipAddressID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("IP Address " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update IP Addresses. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or ipAddressID are not defined. You must define these parameters.").build();
        }
    }
}