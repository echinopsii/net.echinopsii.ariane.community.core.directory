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
package net.echinopsii.ariane.core.directory.wat.rest.technical.network;

import net.echinopsii.ariane.core.directory.base.model.technical.network.Datacenter;
import net.echinopsii.ariane.core.directory.base.model.technical.network.MulticastArea;
import net.echinopsii.ariane.core.directory.base.model.technical.network.Subnet;
import net.echinopsii.ariane.core.directory.base.model.technical.network.SubnetType;
import net.echinopsii.ariane.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.core.directory.wat.json.ToolBox;
import net.echinopsii.ariane.core.directory.wat.json.ds.technical.network.SubnetJSON;
import net.echinopsii.ariane.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.core.directory.wat.rest.technical.system.OSInstanceEndpoint;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
@Path("/directories/common/infrastructure/network/subnets")
public class SubnetEndpoint {
    private static final Logger log = LoggerFactory.getLogger(MulticastAreaEndpoint.class);
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
        TypedQuery<Subnet> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.datacenters LEFT JOIN FETCH l.type LEFT JOIN FETCH l.marea WHERE l.id = :entityId ORDER BY l.id", Subnet.class);
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
        TypedQuery<Subnet> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.datacenters LEFT JOIN FETCH l.type LEFT JOIN FETCH l.marea WHERE l.name = :entityName ORDER BY l.name", Subnet.class);
        findByNameQuery.setParameter("entityName", name);
        Subnet entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static SubnetType findSubnetTypeByName(EntityManager em, String name) {
        TypedQuery<SubnetType> findByNameQuery = em.createQuery("SELECT DISTINCT l FROM SubnetType l WHERE l.name = :entityName ORDER BY l.name", SubnetType.class);
        findByNameQuery.setParameter("entityName", name);
        SubnetType entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static String getAvailableSubnetType(EntityManager em) {
        List<SubnetType> resultList = em.createQuery("SELECT DISTINCT l FROM SubnetType l ORDER BY l.name", SubnetType.class).getResultList();
        List<String> subnetTypeNames = new ArrayList<>();
        for (SubnetType type : resultList)
            subnetTypeNames.add(type.getName());
        return subnetTypeNames.toString();
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displaySubnet(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ccntwadmin") || subject.hasRole("ccntwreviewer") || subject.isPermitted("ccDirComITiNtwSubnet:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
        if (subject.hasRole("ccntwadmin") || subject.hasRole("ccntwreviewer") || subject.isPermitted("ccDirComITiNtwSubnet:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Subnet> results = new HashSet(em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.datacenters LEFT JOIN FETCH l.type LEFT JOIN FETCH l.marea ORDER BY l.id", Subnet.class).getResultList());

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
            if (subject.hasRole("ccntwadmin") || subject.hasRole("ccntwreviewer") || subject.isPermitted("ccDirComITiNtwSubnet:display") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createSubnet(@QueryParam("name")String name, @QueryParam("subnetIP")String subnetIP, @QueryParam("subnetMask")String subnetMask,
                                 @QueryParam("type")String type, @QueryParam("description")String description) {
        if (name!=null && subnetIP!=null && subnetMask!=null && type!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create subnet : ({},{},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, subnetIP, subnetMask, type, description});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetByName(em, name);
                if (entity==null) {
                    SubnetType subnetType = findSubnetTypeByName(em,type);
                    if (subnetType!=null) {
                        entity = new Subnet().setNameR(name).setDescriptionR(description).setSubnetIPR(subnetIP).setSubnetMaskR(subnetMask).setTypeR(subnetType);
                        try {
                            em.getTransaction().begin();
                            em.persist(entity);
                            em.getTransaction().commit();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        Response ret = Response.status(Status.INTERNAL_SERVER_ERROR).
                                                entity("Wrong subnet type " + type + ". Available subnet types are : " + getAvailableSubnetType(em)).build();
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or subnetIP and/or subnetMask and/or type are not defined. " +
                                                                        "You must define these parameters.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteSubnet(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete subnet : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        for (Datacenter dc : entity.getDatacenters())
                            dc.getSubnets().remove(entity);
                        for (OSInstance osi : entity.getOsInstances())
                            osi.getNetworkSubnets().remove(entity);
                        if (entity.getMarea()!=null)
                            entity.getMarea().getSubnets().remove(entity);
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Subnet " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is no defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateSubnetName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateSubnetDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnetip")
    public Response updateSubnetIP(@QueryParam("id")Long id, @QueryParam("subnetIP")String subnetIP) {
        if (id!=0 && subnetIP!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} IP : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetIP});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/subnetmask")
    public Response updateSubnetMask(@QueryParam("id")Long id, @QueryParam("subnetMask")String subnetMask) {
        if (id!=0 && subnetMask!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} mask : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, subnetMask});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/type")
    public Response updateSubnetType(@QueryParam("id")Long id, @QueryParam("type")String type) {
        if (id!=0 && type!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, type});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    SubnetType subnetType = findSubnetTypeByName(em,type);
                    if (subnetType!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getType()!=null)
                                entity.getType().getSubnets().remove(entity);
                            subnetType.getSubnets().add(entity);
                            entity.setType(subnetType);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with type " + type).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        Response ret = Response.status(Status.INTERNAL_SERVER_ERROR).
                                                       entity("Wrong subnet type " + type + ". Available subnet types are : " + getAvailableSubnetType(em)).build();
                        em.close();
                        return ret;
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/multicastarea")
    public Response updateSubnetMulticastArea(@QueryParam("id")Long id, @QueryParam("multicastareaID")Long mareaID) {
        if (id!=0 && mareaID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} multicast area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, mareaID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    MulticastArea marea = MulticastAreaEndpoint.findMulticastAreaById(em, mareaID);
                    if (marea!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getMarea()!=null)
                                entity.getMarea().getSubnets().remove(entity);
                            marea.getSubnets().add(entity);
                            entity.setMarea(marea);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with multicast area " + mareaID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        if (mareaID==-1) {
                            try {
                                em.getTransaction().begin();
                                if (entity.getMarea()!=null)
                                    entity.getMarea().getSubnets().remove(entity);
                                entity.setMarea(null);
                                em.getTransaction().commit();
                                em.close();
                                return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated with multicast area " + mareaID).build();
                            } catch (Throwable t) {
                                if(em.getTransaction().isActive())
                                    em.getTransaction().rollback();
                                em.close();
                                return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                            }
                        }
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Multicast area " + mareaID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/add")
    public Response updateSubnetAddOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by adding os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/delete")
    public Response updateSubnetDeleteOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by deleting os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/datacenters/add")
    public Response updateSubnetAddDatacenter(@QueryParam("id")Long id, @QueryParam("datacenterID")Long dcID) {
        if (id!=0 && dcID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by adding datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, dcID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    Datacenter datacenter = DatacenterEndpoint.findDatacenterById(em, dcID);
                    if (datacenter!=null) {
                        try {
                            em.getTransaction().begin();
                            datacenter.getSubnets().add(entity);
                            entity.getDatacenters().add(datacenter);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated by adding datacenter " + dcID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Datacenter " + dcID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/datacenters/delete")
    public Response updateSubnetDeleteDatacenter(@QueryParam("id")Long id, @QueryParam("datacenterID")Long dcID) {
        if (id!=0 && dcID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update subnet {} by adding datacenter : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, dcID});
            if (subject.hasRole("ccntwadmin") || subject.isPermitted("ccDirComITiNtwSubnet:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Subnet entity = findSubnetById(em, id);
                if (entity!=null) {
                    Datacenter datacenter = DatacenterEndpoint.findDatacenterById(em, dcID);
                    if (datacenter!=null) {
                        try {
                            em.getTransaction().begin();
                            datacenter.getSubnets().remove(entity);
                            entity.getDatacenters().remove(datacenter);
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Subnet " + id + " has been successfully updated by removing datacenter " + dcID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating subnet " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Datacenter " + dcID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Subnet " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update subnets. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }
}