/**
 * Directory wat
 * Multicast Area REST endpoint
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

import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Datacenter;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.MulticastArea;
import net.echinopsii.ariane.community.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.community.core.directory.wat.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.wat.json.ds.technical.network.MulticastAreaJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
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
@Path("/directories/common/infrastructure/network/multicastareas")
public class MulticastAreaEndpoint {
    private static final Logger log = LoggerFactory.getLogger(MulticastAreaEndpoint.class);
    private EntityManager em;

    public static Response multicastAreaToJSON(MulticastArea entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            MulticastAreaJSON.oneMulticastArea2JSON(entity, outStream);
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

    public static MulticastArea findMulticastAreaById(EntityManager em, long id) {
        TypedQuery<MulticastArea> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM MulticastArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.datacenters WHERE m.id = :entityId ORDER BY m.id", MulticastArea.class);
        findByIdQuery.setParameter("entityId", id);
        MulticastArea entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static MulticastArea findMulticastAreaByName(EntityManager em, String name) {
        TypedQuery<MulticastArea> findByNameQuery = em.createQuery("SELECT DISTINCT m FROM MulticastArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.datacenters WHERE m.name = :entityName ORDER BY m.name", MulticastArea.class);
        findByNameQuery.setParameter("entityName", name);
        MulticastArea entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayMulticastArea(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get multicast area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ccntwadmin") || subject.hasRole("ccntwreviewer") || subject.isPermitted("ccDirComITiNtwMarea:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            MulticastArea entity = findMulticastAreaById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = multicastAreaToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display multicast areas. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllMulticastAreas() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get multicast areas", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ccntwadmin") || subject.hasRole("ccntwreviewer") || subject.isPermitted("ccDirComITiNtwMarea:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<MulticastArea> results = new HashSet(em.createQuery("SELECT DISTINCT m FROM MulticastArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.datacenters ORDER BY m.id", MulticastArea.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                MulticastAreaJSON.manyMulticastAreas2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display multicast areas. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getMulticatArea(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
            return displayMulticastArea(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get multicast area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ccntwadmin") || subject.hasRole("ccntwreviewer") || subject.isPermitted("ccDirComITiNtwMarea:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = multicastAreaToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createMulticastArea(@QueryParam("name")String name, @QueryParam("description")String description) {
        if (name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create multicast area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaByName(em, name);
                if (entity == null) {
                    entity = new MulticastArea().setNameR(name).setDescriptionR(description);
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

                Response ret = multicastAreaToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteMulticastArea(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete multicast area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        for (Datacenter datacenter : entity.getDatacenters())
                            datacenter.getMulticastAreas().remove(entity);
                        for (Subnet subnet : entity.getSubnets())
                            subnet.setMarea(null);
                        em.remove(entity);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Multicast area " + id + " has been successfully deleted").build();
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateMulticastAreaName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update multicast area {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Multicast area " + id + " has been successfully updated with name " + name).build();
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateMulticastAreaDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update multicast area {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescription(description);
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Multicast area " + id + " has been successfully updated with description " + description).build();
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
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/datacenters/add")
    public Response updateMulticastAreaAddDatacenter(@QueryParam("id")Long id,  @QueryParam("datacenterID")Long dcID) {
        if (id!=0 && dcID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update multicast area {} by adding datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, dcID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaById(em, id);
                if (entity!=null) {
                    Datacenter datacenter = DatacenterEndpoint.findDatacenterById(em, dcID);
                    if (datacenter!=null) {
                        try {
                            em.getTransaction().begin();
                            datacenter.getMulticastAreas().add(entity);
                            entity.getDatacenters().add(datacenter);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Multicast area " + id + " has been successfully updated by adding datacenter " + dcID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Datacenter " + dcID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Multicast area " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or dcID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/datacenters/delete")
    public Response updateMulticastAreaDeleteDatacenter(@QueryParam("id")Long id,  @QueryParam("datacenterID")Long dcID) {
        if (id!=0 && dcID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update multicast area {} by deleting datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, dcID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaById(em, id);
                if (entity!=null) {
                    Datacenter datacenter = DatacenterEndpoint.findDatacenterById(em, dcID);
                    if (datacenter!=null) {
                        try {
                            em.getTransaction().begin();
                            datacenter.getMulticastAreas().remove(entity);
                            entity.getDatacenters().remove(datacenter);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Multicast area " + id + " has been successfully updated by deleting datacenter " + dcID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating datacenter " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Datacenter " + dcID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Multicast area " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or dcID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/add")
    public Response updateMulticastAreaAddSubnet(@QueryParam("id")Long id, @QueryParam("subnetID")Long subnetID) {
        if (id!=0 && subnetID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update multicast area {} by adding subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaById(em, id);
                if (entity!=null) {
                    Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                    if (subnet!=null) {
                        try {
                            em.getTransaction().begin();
                            if (subnet.getMarea()!=null && !subnet.getMarea().equals(entity))
                                subnet.getMarea().getSubnets().remove(subnet);
                            subnet.setMarea(entity);
                            entity.getSubnets().add(subnet);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Multicast area " + id + " has been successfully updated by adding subnet " + subnetID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
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
                    return Response.status(Status.NOT_FOUND).entity("Multicast area " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnets/delete")
    public Response updateMulticastAreaDeleteSubnet(@QueryParam("id")Long id, @QueryParam("subnetID")Long subnetID) {
        if (id!=0 && subnetID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update multicast area {} by deleting subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwMarea:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                MulticastArea entity = findMulticastAreaById(em, id);
                if (entity!=null) {
                    Subnet subnet = SubnetEndpoint.findSubnetById(em, subnetID);
                    if (subnet!=null) {
                        try {
                            em.getTransaction().begin();
                            subnet.setMarea(null);
                            entity.getSubnets().remove(subnet);
                            em.getTransaction().commit();
                            return Response.status(Status.OK).entity("Multicast area " + id + " has been successfully updated by deleting subnet " + subnetID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
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
                    return Response.status(Status.NOT_FOUND).entity("Multicast area " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update multicast areas. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or subnetID are not defined. You must define these parameters.").build();
        }
    }
}