/**
 * Directory wat
 * Application REST endpoint
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

import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Team;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.base.json.ds.organisational.ApplicationJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Application;
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
@Path("directories/common/organisation/applications")
public class ApplicationEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ApplicationEndpoint.class);
    private EntityManager em;

    public static Response applicationToJSON(Application application) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            ApplicationJSON.oneApplication2JSON(application, outStream);
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

    public static Application findApplicationById(EntityManager em, long id) {
        TypedQuery<Application> findByIdQuery = em.createQuery("SELECT DISTINCT a FROM Application a LEFT JOIN FETCH a.osInstances LEFT JOIN FETCH a.company WHERE a.id = :entityId ORDER BY a.id", Application.class);
        findByIdQuery.setParameter("entityId", id);
        Application entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static Application findApplicationByName(EntityManager em, String name) {
        TypedQuery<Application> findByNameQuery = em.createQuery("SELECT DISTINCT a FROM Application a LEFT JOIN FETCH a.osInstances LEFT JOIN FETCH a.company WHERE a.name = :entityName ORDER BY a.name", Application.class);
        findByNameQuery.setParameter("entityName", name);
        Application entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayApplication(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgApp:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            Application entity = findApplicationById(em, id);
            if (entity == null){
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = applicationToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display applications. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllApplications() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get applications", new Object[]{Thread.currentThread().getId(),subject.getPrincipal()});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgApp:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Application> results = new HashSet(em.createQuery("SELECT DISTINCT a FROM Application a LEFT JOIN FETCH a.osInstances LEFT JOIN FETCH a.company ORDER BY a.id", Application.class).getResultList());
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Response ret = null;
            try {
                ApplicationJSON.manyApplications2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display applications. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getApplication(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
            return displayApplication(id);
        } else if (name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgApp:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = applicationToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createApplication(@QueryParam("name")String name, @QueryParam("shortName")String shortName,
                                      @QueryParam("description")String description, @QueryParam("colorCode")String colorCode) {
        if (name!=null && shortName!=null && colorCode!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create application : ({},{},{},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, shortName, description, colorCode});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationByName(em, name);
                if (entity == null) {
                    entity = new Application();
                    entity.setNameR(name).setShortNameR(shortName).setColorCodeR(colorCode).setDescription(description);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.flush();
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating application " + entity.getName() + " : " + t.getMessage()).build();
                    }
                }

                Response ret = applicationToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or short name and/or color code are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteApplication(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        for (OSInstance osInstance : entity.getOsInstances())
                            osInstance.getApplications().remove(entity);
                        entity.getCompany().getApplications().remove(entity);
                        entity.getTeam().getApplications().remove(entity);
                        em.remove(entity);
                        em.flush();
                        em.getTransaction().commit();
                        return Response.status(Status.OK).entity("Application " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting application " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateApplicationName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Application " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/shortName")
    public Response updateApplicationShortName(@QueryParam("id")Long id, @QueryParam("shortName")String shortName) {
        if (id!=0 && shortName!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} short name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, shortName});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setShortName(shortName);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Application " + id + " has been successfully updated with short name " + shortName).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or shortName are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateApplicationDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescription(description);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Application " + id + " has been successfully updated with description " + description).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/colorCode")
    public Response updateApplicationColorCode(@QueryParam("id")Long id, @QueryParam("colorCode")String colorCode) {
        if (id!=0 && colorCode!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} color code : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, colorCode});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setColorCode(colorCode);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Application " + id + " has been successfully updated with color code " + colorCode).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or colorCode are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/company")
    public Response updateApplicationCompany(@QueryParam("id")Long id, @QueryParam("companyID")Long companyID) {
        if (id!=0 && companyID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} company : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, companyID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    Company company = CompanyEndpoint.findCompanyById(em, companyID);
                    if (company != null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getCompany()!=null)
                                entity.getCompany().getApplications().remove(entity);
                            entity.setCompany(company);
                            company.getApplications().add(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Application " + id + " has been successfully updated with company " + companyID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("Company " + companyID + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Application " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or companyID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/team")
    public Response updateApplicationTeam(@QueryParam("id")Long id, @QueryParam("teamID")Long teamID) {
        if (id!=0 && teamID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} team : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, teamID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    Team team = TeamEndpoint.findTeamById(em, teamID);
                    if (team != null) {
                        try {
                            em.getTransaction().begin();
                            entity.getTeam().getApplications().remove(entity);
                            entity.setTeam(team);
                            team.getApplications().add(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Application " + id + " has been successfully updated with team " + teamID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("Team " + teamID + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Application " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or teamID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/add")
    public Response updateApplicationAddOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} by adding os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance != null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsInstances().add(osInstance);
                            osInstance.getApplications().add(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Application " + id + " has been suessfully updated by adding os instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("OS Instance " + osiID + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Application " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or teamID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/delete")
    public Response updateApplicationDeleteOSInstance(@QueryParam("id")Long id, @QueryParam("osiID")Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update application {} by adding os instance : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgApp:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Application entity = findApplicationById(em, id);
                if (entity != null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance != null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsInstances().remove(osInstance);
                            osInstance.getApplications().remove(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Application " + id + " has been suessfully updated by adding os instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating application " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("OS Instance " + osiID + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Application " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update applications. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or teamID are not defined. You must define these parameters.").build();
        }
    }
}