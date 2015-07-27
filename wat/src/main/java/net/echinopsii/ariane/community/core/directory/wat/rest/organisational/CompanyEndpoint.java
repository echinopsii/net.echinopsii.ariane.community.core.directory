/**
 * Directory wat
 * Companies REST endpoint
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
import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSType;
import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.base.json.ds.organisational.CompanyJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.rest.technical.system.OSTypeEndpoint;
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

import static net.echinopsii.ariane.community.core.directory.base.json.ds.organisational.CompanyJSON.JSONFriendlyCompany;

/**
 *
 */
@Path("directories/common/organisation/companies")
public class CompanyEndpoint {
    private static final Logger log = LoggerFactory.getLogger(CompanyEndpoint.class);
    private EntityManager em;

    public static Response companyToJSON(Company entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            CompanyJSON.oneCompany2JSON(entity, outStream);
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

    public static Company findCompanyById(EntityManager em, Long id) {
        TypedQuery<Company> findByIdQuery = em.createQuery("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.applications WHERE c.id = :entityId ORDER BY c.id", Company.class);
        findByIdQuery.setParameter("entityId", id);
        Company entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static Company findCompanyByName(EntityManager em, String name) {
        TypedQuery<Company> findByNameQuery = em.createQuery("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.applications WHERE c.name = :entityName ORDER BY c.name", Company.class);
        findByNameQuery.setParameter("entityName", name);
        Company entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyCompany jsonFriendlyCompany) {
        Company entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if(jsonFriendlyCompany.getCompanyID() !=0)
            entity = findCompanyById(em, jsonFriendlyCompany.getCompanyID());
        if(entity == null && jsonFriendlyCompany.getCompanyID()!=0){
            commonRestResponse.setErrorMessage("Request Error : provided Company ID " + jsonFriendlyCompany.getCompanyID() +" was not found.");
            return commonRestResponse;
        }
        if(entity == null){
            if(jsonFriendlyCompany.getCompanyName() != null){
                entity = findCompanyByName(em, jsonFriendlyCompany.getCompanyName());
            }
        }
        if(entity != null){
            if (jsonFriendlyCompany.getCompanyName() !=null) {
                entity.setName(jsonFriendlyCompany.getCompanyName());
            }
            if (jsonFriendlyCompany.getCompanyDescription() != null) {
                entity.setDescription(jsonFriendlyCompany.getCompanyDescription());
            }
            if(jsonFriendlyCompany.getCompanyApplicationsID() != null) {
                if (!jsonFriendlyCompany.getCompanyApplicationsID().isEmpty()) {
                    for (Application application : entity.getApplications()) {
                        if (!jsonFriendlyCompany.getCompanyApplicationsID().contains(application.getId())) {
                            entity.getApplications().remove(application);
                            application.setCompany(null);
                        }
                    }
                    for (Long appid : jsonFriendlyCompany.getCompanyApplicationsID()) {
                        Application application = ApplicationEndpoint.findApplicationById(em, appid);
                        if (application != null) {
                            if (!entity.getApplications().contains(application)) {
                                entity.getApplications().add(application);
                                application.setCompany(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Company. Reason : provided Application ID " + appid +" was not found.");
                            return commonRestResponse;
                        }
                    }
                } else {
                    for (Application application : entity.getApplications()) {
                        entity.getApplications().remove(application);
                        application.setCompany(null);
                    }
                }
            }
            if(jsonFriendlyCompany.getCompanyOSTypesID() != null) {
                if (!jsonFriendlyCompany.getCompanyOSTypesID().isEmpty()) {
                    for (OSType osType: entity.getOsTypes()) {
                        if (!jsonFriendlyCompany.getCompanyOSTypesID().contains(osType.getId())) {
                            entity.getOsTypes().remove(osType);
                            osType.setCompany(null);
                        }
                    }
                    for (Long osTypeid : jsonFriendlyCompany.getCompanyOSTypesID()) {
                        OSType osType = OSTypeEndpoint.findOSTypeById(em, osTypeid);
                        if (osType != null) {
                            if (!entity.getOsTypes().contains(osType)) {
                                entity.getOsTypes().add(osType);
                                osType.setCompany(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Company. Reason : provided OSType ID " + osTypeid +" was not found.");
                            return  commonRestResponse;
                        }
                    }
                } else {
                    for (OSType osType: entity.getOsTypes()) {
                        entity.getOsTypes().remove(osType);
                        osType.setCompany(null);
                    }
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new Company();
            entity.setNameR(jsonFriendlyCompany.getCompanyName()).setDescription(jsonFriendlyCompany.getCompanyDescription());
            if (jsonFriendlyCompany.getCompanyApplicationsID() != null) {
                if (!jsonFriendlyCompany.getCompanyApplicationsID().isEmpty()) {
                    for (Long appid : jsonFriendlyCompany.getCompanyApplicationsID()) {
                        Application application = ApplicationEndpoint.findApplicationById(em, appid);
                        if (application != null) {
                            if (!entity.getApplications().contains(application)) {
                                entity.getApplications().add(application);
                                application.setCompany(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to create Company. Reason : provided Application ID " + appid +" was not found.");
                            return commonRestResponse;
                        }
                    }
                }
            }

            if (jsonFriendlyCompany.getCompanyOSTypesID() != null) {
                if (!jsonFriendlyCompany.getCompanyOSTypesID().isEmpty()) {
                    for (Long osTypeid : jsonFriendlyCompany.getCompanyOSTypesID()) {
                        OSType osType = OSTypeEndpoint.findOSTypeById(em, osTypeid);
                        if (osType != null) {
                            if (!entity.getOsTypes().contains(osType)) {
                                entity.getOsTypes().add(osType);
                                osType.setCompany(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to create Company. Reason : provided OSType ID " + osTypeid +" was not found.");
                            return  commonRestResponse;
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
    public Response displayCompany(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get company : {},{}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgCompany:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            Company entity = findCompanyById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = companyToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display companies. Contact your administrator.").build();
        }
    }

    @GET
    public Response DisplayAllCompanies() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get company : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgCompany:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<Company> results = new HashSet(em.createQuery("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.applications ORDER BY c.id", Company.class).getResultList());
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            Response ret = null;
            try {
                CompanyJSON.manyCompanies2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display companies. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getCompany(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
             return displayCompany(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get company : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("orgadmin") || subject.hasRole("orgreviewer") || subject.isPermitted("dirComOrgCompany:display") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = companyToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createCompany(@QueryParam("name")String name, @QueryParam("description")String description) {
        if (name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create company : {},{}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, description});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyByName(em, name);
                if (entity == null) {
                    entity = new Company().setNameR(name).setDescriptionR(description);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.flush();
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating company " + entity.getName() + " : " + t.getMessage()).build();
                    }
                }

                Response ret = companyToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name is not defined. You must define this parameter.").build();
        }
    }

    @POST
    public Response postCompany(@QueryParam("payload") String payload) throws IOException {

        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update company : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyCompany jsonFriendlyCompany = CompanyJSON.JSON2Company(payload);
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyCompany);
            Company entity = (Company) commonRestResponse.getDeserializedObject();
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
                    Response ret = companyToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating company " + payload + " : " + t.getMessage()).build();
                }
            } else{
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create companies. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteCompany(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete company : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        for (OSType osType : entity.getOsTypes())
                            osType.setCompany(null);
                        for (Application application : entity.getApplications())
                            application.setCompany(null);
                        em.remove(entity);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Company " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting company " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateCompanyName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update company {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Company " + id + " has been successfully updated with name " + name).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating company " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/description")
    public Response updateCompanyDescription(@QueryParam("id")Long id, @QueryParam("description")String description) {
        if (id!=0 && description!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update company {} description : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, description});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyById(em, id);
                if (entity != null) {
                    try {
                        em.getTransaction().begin();
                        entity.setDescription(description);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("Company " + id + " has been successfully updated with description " + description).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating company " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or description are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/applications/add")
    public Response updateCompanyAddApplication(@QueryParam("id")Long id, @QueryParam("applicationID")Long applicationID) {
        if (id!=0 && applicationID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update company {} by adding application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, applicationID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyById(em, id);
                if (entity != null) {
                    Application application = ApplicationEndpoint.findApplicationById(em, applicationID);
                    if (application!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getApplications().add(application);
                            if (application.getCompany()!=null && !application.getColorCode().equals(entity))
                                application.getCompany().getApplications().remove(application);
                            application.setCompany(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Company " + id + " has been successfully updated by adding application " + applicationID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating company " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("Application " + id + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Company " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or applicationID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/applications/delete")
    public Response updateCompanyDeleteApplication(@QueryParam("id")Long id, @QueryParam("applicationID")Long applicationID) {
        if (id!=0 && applicationID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update company {} by deleting application : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, applicationID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyById(em, id);
                if (entity != null) {
                    Application application = ApplicationEndpoint.findApplicationById(em, applicationID);
                    if (application!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getApplications().remove(application);
                            if (application.getColorCode()!=null && application.getCompany().equals(entity))
                                application.setCompany(null);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Company " + id + " has been successfully updated by deleting application " + applicationID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating company " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("Application " + id + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Company " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or applicationID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/ostypes/add")
    public Response updateCompanyAddOSType(@QueryParam("id")Long id, @QueryParam("ostypeID")Long ostypeID) {
        if (id!=0 && ostypeID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update company {} by adding OS Type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ostypeID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyById(em, id);
                if (entity != null) {
                    OSType osType = OSTypeEndpoint.findOSTypeById(em, ostypeID);
                    if (osType!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsTypes().add(osType);
                            if (osType.getCompany()!=null && !osType.getCompany().equals(entity))
                                osType.getCompany().getOsTypes().remove(osType);
                            osType.setCompany(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Company " + id + " has been successfully updated by adding os type " + ostypeID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating company " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("OS Type " + id + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Company " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or ostypeID are not defined. You must define these parameters.").build();
        }
    }

    @GET
    @Path("/update/ostypes/delete")
    public Response updateCompanyDeleteOSType(@QueryParam("id")Long id, @QueryParam("ostypeID")Long ostypeID) {
        if (id!=0 && ostypeID!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update company {} by deleting OS Type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, ostypeID});
            if (subject.hasRole("orgadmin") || subject.isPermitted("dirComOrgCompany:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                Company entity = findCompanyById(em, id);
                if (entity != null) {
                    OSType osType = OSTypeEndpoint.findOSTypeById(em, ostypeID);
                    if (osType!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsTypes().remove(osType);
                            if (osType.getCompany()!=null && osType.getCompany().equals(entity))
                                osType.setCompany(null);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("Company " + id + " has been successfully updated by deleting os type " + ostypeID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating company " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        return Response.status(Status.NOT_FOUND).entity("OS Type " + id + " not found.").build();
                    }
                } else {
                    return Response.status(Status.NOT_FOUND).entity("Company " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update companies. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or ostypeID are not defined. You must define these parameters.").build();
        }
    }
}