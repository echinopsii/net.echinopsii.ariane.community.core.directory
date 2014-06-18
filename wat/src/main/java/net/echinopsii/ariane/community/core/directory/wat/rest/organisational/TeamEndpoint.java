/**
 * Directory wat
 * Team REST endpoint
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

import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.wat.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.wat.json.ds.organisational.TeamJSON;
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
@Path("directories/common/organisation/teams")
public class TeamEndpoint {
    private static final Logger log = LoggerFactory.getLogger(TeamEndpoint.class);
    private EntityManager em;

    public static Response teamToJSON(Team team) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            TeamJSON.oneTeam2JSON(team, outStream);
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

    public static Team findTeamById(EntityManager em, long id) {
        TypedQuery<Team> findByIdQuery = em.createQuery("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.osInstances WHERE t.id = :entityId ORDER BY t.id", Team.class);
        findByIdQuery.setParameter("entityId", id);
        Team entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static Team findTeamByName(EntityManager em, String name) {
        TypedQuery<Team> findByNameQuery = em.createQuery("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.osInstances WHERE t.name = :entityName ORDER BY t.name", Team.class);
        findByNameQuery.setParameter("entityName", name);
        Team entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayTeam(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get team : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgTeam:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            Team entity = findTeamById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = teamToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display teams. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllTeams() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get teams", new Object[]{Thread.currentThread().getId(),subject.getPreviousPrincipals()});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgTeam:display") ||
                    subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Team> results = new HashSet(em.createQuery("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.osInstances ORDER BY t.id", Team.class).getResultList());
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Response ret = null;
            try {
                TeamJSON.manyTeams2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display teams. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getTeam(@QueryParam("name")String name, @QueryParam("id")long id){
        if (id!=0) {
            return displayTeam(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get team : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgTeam:display") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = teamToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createTeam(@QueryParam("name")String name, @QueryParam("description")String description, @QueryParam("colorCode")String colorCode) {
        if (name != null && colorCode != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create team : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamByName(em, name);
                if (entity == null) {
                    entity = new Team();
                    entity.setNameR(name).setDescriptionR(description).setColorCode(colorCode);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.flush();
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating team " + entity.getName() + " : " + t.getMessage()).build();
                    }
                }

                Response ret = teamToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or colorCode are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteTeam(@QueryParam("id")Long id) {
        if (id != 0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete team : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        for (Application application : entity.getApplications())
                            application.setTeam(null);
                        for (OSInstance osInstance : entity.getOsInstances())
                            osInstance.getTeams().remove(entity);
                        em.remove(entity);
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Team " + id + " has been successfully removed").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting team " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateTeamName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update team {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Team " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating team " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateTeamDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update team {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescription(description);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Team " + id + " has been successfully updated with description " + description).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating team " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/colorCode")
    public Response updateTeamColorCode(@QueryParam("id")Long id, @QueryParam("colorCode")String colorCode) {
        if (id!=0 && colorCode != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update team {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, colorCode});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setColorCode(colorCode);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Team " + id + " has been successfully updated with color code " + colorCode).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating team " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or color code are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/applications/add")
    public Response updateTeamAddApplication(@QueryParam("id")Long id, @QueryParam("applicationID") Long applicationID) {
        if (id!=0 && applicationID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update team {} by adding application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, applicationID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    Application application = ApplicationEndpoint.findApplicationById(em, applicationID);
                    if (application!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getApplications().add(application);
                            if (application.getTeam()!=null)
                                application.getTeam().getApplications().remove(application);
                            application.setTeam(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Team " + id + " has been successfully updated by adding application " + applicationID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating team " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Application " + applicationID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Team " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or application id are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/applications/delete")
    public Response updateTeamDeleteApplication(@QueryParam("id")Long id, @QueryParam("applicationID") Long applicationID) {
        if (id!=0 && applicationID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update team {} by deleting application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, applicationID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    Application application = ApplicationEndpoint.findApplicationById(em, applicationID);
                    if (application!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getApplications().remove(application);
                            application.setTeam(null);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Team " + id + " has been successfully updated by removing application " + applicationID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating team " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Application " + applicationID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Team " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or application id are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/add")
    public Response updateTeamAddOSInstance(@QueryParam("id")Long id, @QueryParam("osiID") Long osiID) {
        if (id!=0 && osiID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update team {} by adding OS instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em,osiID);
                    if (osInstance!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsInstances().add(osInstance);
                            osInstance.getTeams().add(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Team " + id + " has been successfully updated by adding OS instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating team " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS Instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Team " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or os instance id are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/delete")
    public Response updateTeamDeleteOSInstance(@QueryParam("id")Long id, @QueryParam("osiID") Long osiID) {
        if (id!=0 && osiID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update team {} by adding OS instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgTeam:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Team entity = findTeamById(em, id);
                if (entity!=null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em,osiID);
                    if (osInstance!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsInstances().remove(osInstance);
                            osInstance.getTeams().remove(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Team " + id + " has been successfully updated by removing OS instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating team " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS Instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("Team " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update teams. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or os instance id are not defined. You must define these parameters.").build();
        }
    }
}