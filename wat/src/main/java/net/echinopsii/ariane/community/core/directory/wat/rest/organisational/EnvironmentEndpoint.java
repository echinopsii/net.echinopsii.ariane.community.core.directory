/**
 * Directory wat
 * Environment REST endpoint
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
package net.echinopsii.ariane.community.core.directory.wat.rest.organisational;

import net.echinopsii.ariane.community.core.directory.base.model.organisational.Environment;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.wat.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.wat.json.ds.organisational.EnvironmentJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
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
import java.util.HashSet;

/**
 *
 */
@Path("directories/common/organisation/environments")
public class EnvironmentEndpoint {
    private static final Logger log = LoggerFactory.getLogger(EnvironmentEndpoint.class);
    private EntityManager em;

    private static Response environmentToJSON(Environment entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            EnvironmentJSON.oneEnvironment2JSON(entity, outStream);
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

    public static Environment findEnvironmentByID(EntityManager em, Long id) {
        TypedQuery<Environment> findByIdQuery = em.createQuery("SELECT DISTINCT e FROM Environment e LEFT JOIN FETCH e.osInstances WHERE e.id = :entityId ORDER BY e.id", Environment.class);
        findByIdQuery.setParameter("entityId", id);
        Environment entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity=null;
        }
        return entity;
    }

    public static Environment findEnvironmentByName(EntityManager em, String name) {
        TypedQuery<Environment> findByNameQuery = em.createQuery("SELECT DISTINCT e FROM Environment e LEFT JOIN FETCH e.osInstances WHERE e.name = :entityName ORDER BY e.name", Environment.class);
        findByNameQuery.setParameter("entityName", name);
        Environment entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity=null;
        }
        return entity;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayEnvironment(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get environment : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgEnvironment:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            Environment entity = findEnvironmentByID(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = environmentToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display environments. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllEnvironments() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get environments", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgEnvironment:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Environment> results = new HashSet(em.createQuery("SELECT DISTINCT e FROM Environment e LEFT JOIN FETCH e.osInstances ORDER BY e.id", Environment.class).getResultList());
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Response ret = null;
            try {
                EnvironmentJSON.manyEnvironments2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display environments. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getEnvironment(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
            return displayEnvironment(id);
        } else if (name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get environment : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgEnvironment:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Environment entity = findEnvironmentByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = environmentToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display environments. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createEnvironment(@QueryParam("name")String name, @QueryParam("description")String description) {
        if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create environment : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, description});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgEnvironment:create") ||
                subject.hasRole("jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Environment environment = findEnvironmentByName(em, name);
                if (environment == null) {
                    environment = new Environment();
                    environment.setName(name);
                    environment.setDescription(description);
                    try {
                        em.getTransaction().begin();
                        em.persist(environment);
                        em.flush();
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating environment " + environment.getName() + " : " + t.getMessage()).build();
                    }
                }

                Response ret = environmentToJSON(environment);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create environments. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteEnvironment(@QueryParam("id")Long id) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete environment : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgEnvironment:remove") ||
                subject.hasRole("jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Environment environment = findEnvironmentByID(em, id);
                if (environment == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                } else {
                    try {
                        em.getTransaction().begin();
                        for (OSInstance osInstance : environment.getOsInstances())
                            osInstance.getEnvironments().remove(environment);
                        em.remove(environment);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Environment " + id + " has been successfully removed").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting environment " + environment.getName() + " : " + t.getMessage()).build();
                    }
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete environments. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateEnvironmentName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id != 0 && name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update environment name : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgEnvironment:update") ||
                subject.hasRole("jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Environment environment = findEnvironmentByID(em, id);
                if (environment == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                } else {
                    try {
                        em.getTransaction().begin();
                        environment.setName(name);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Environment " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating environment " + environment.getName() + " : " + t.getMessage()).build();
                    }
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update environments. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateEnvironmentDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id != 0 && description != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update environment description : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgEnvironment:update") ||
                subject.hasRole("jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Environment environment = findEnvironmentByID(em, id);
                if (environment == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                } else {
                    try {
                        em.getTransaction().begin();
                        environment.setDescription(description);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Environment " + id + " has been successfully updated with description " + description).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating environment " + environment.getName() + " : " + t.getMessage()).build();
                    }
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update environments. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/add")
    public Response updateEnvironmentAddOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id != 0 && osiID != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update environment by adding new os instance : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgEnvironment:update") ||
                        subject.hasRole("jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Environment environment = findEnvironmentByID(em, id);
                if (environment == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Environment with id " + id + " not found.").build();
                } else {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance == null) {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS instance with id " + osiID + " not found.").build();
                    } else {
                        try {
                            em.getTransaction().begin();
                            environment.getOsInstances().add(osInstance);
                            osInstance.getEnvironments().add(environment);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Environment " + id + " has been successfully updated by addind OS instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating environment " + environment.getName() + " : " + t.getMessage()).build();
                        }
                    }
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update environments. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or osiID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/delete")
    public Response updateEnvironmentDeleteOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id != 0 && osiID != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update environment by adding new os instance : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgEnvironment:update") ||
                subject.hasRole("jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Environment environment = findEnvironmentByID(em, id);
                if (environment == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Environment with id " + id + " not found.").build();
                } else {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance == null) {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS instance with id " + osiID + " not found.").build();
                    } else {
                        try {
                            em.getTransaction().begin();
                            environment.getOsInstances().remove(osInstance);
                            osInstance.getEnvironments().remove(environment);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Environment " + id + " has been successfully updated by addind OS instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating environment " + environment.getName() + " : " + t.getMessage()).build();
                        }
                    }
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update environments. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or osiID are not defined. You must define these parameters.").build();
        }
    }
}