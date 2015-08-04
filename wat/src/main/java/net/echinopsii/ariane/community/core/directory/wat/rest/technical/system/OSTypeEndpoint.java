/**
 * Directory wat
 * OS Type REST endpoint
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

import net.echinopsii.ariane.community.core.directory.base.model.organisational.Company;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSInstance;
import net.echinopsii.ariane.community.core.directory.base.model.technical.system.OSType;
import net.echinopsii.ariane.community.core.directory.base.json.ToolBox;
import net.echinopsii.ariane.community.core.directory.base.json.ds.technical.system.OSTypeJSON;
import net.echinopsii.ariane.community.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import net.echinopsii.ariane.community.core.directory.wat.rest.CommonRestResponse;
import net.echinopsii.ariane.community.core.directory.wat.rest.organisational.CompanyEndpoint;
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

import static net.echinopsii.ariane.community.core.directory.base.json.ds.technical.system.OSTypeJSON.JSONFriendlyOSType;

/**
 *
 */
@Path("/directories/common/infrastructure/system/ostypes")
public class OSTypeEndpoint {
    private static final Logger log = LoggerFactory.getLogger(OSTypeEndpoint.class);
    private EntityManager em;

    public Response osTypeToJSON(OSType entity) {
        Response ret = null;
        String result;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            OSTypeJSON.oneOSType2JSON(entity, outStream);
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

    public static OSType findOSTypeById(EntityManager em, long id) {
        TypedQuery<OSType> findByIdQuery = em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances WHERE o.id = :entityId ORDER BY o.id", OSType.class);
        findByIdQuery.setParameter("entityId", id);
        OSType entity;
        try {
            entity = findByIdQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static OSType findOSTypeByName(EntityManager em, String name) {
        TypedQuery<OSType> findByNameQuery = em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances WHERE o.name = :entityName ORDER BY o.name", OSType.class);
        findByNameQuery.setParameter("entityName", name);
        OSType entity;
        try {
            entity = findByNameQuery.getSingleResult();
        } catch (NoResultException nre) {
            entity = null;
        }
        return entity;
    }

    public static CommonRestResponse jsonFriendlyToHibernateFriendly(EntityManager em, JSONFriendlyOSType jsonFriendlyOSType) {
        OSType entity = null;
        CommonRestResponse commonRestResponse = new CommonRestResponse();

        if(jsonFriendlyOSType.getOsTypeID() !=0)
            entity = findOSTypeById(em, jsonFriendlyOSType.getOsTypeID());
        if(entity == null && jsonFriendlyOSType.getOsTypeID()!=0){
            commonRestResponse.setErrorMessage("Request Error : provided OSType ID " + jsonFriendlyOSType.getOsTypeID() +" was not found.");
            return commonRestResponse;
        }
        if(entity == null){
            if(jsonFriendlyOSType.getOsTypeName() != null){
                entity = findOSTypeByName(em, jsonFriendlyOSType.getOsTypeName());
            }
        }
        if(entity != null) {
            if (jsonFriendlyOSType.getOsTypeName() !=null) {
                entity.setName(jsonFriendlyOSType.getOsTypeName());
            }
            if (jsonFriendlyOSType.getOsTypeArchitecture() != null) {
                entity.setArchitecture(jsonFriendlyOSType.getOsTypeArchitecture());
            }
            if (jsonFriendlyOSType.getOsTypeCompanyID() != 0) {
                Company company = CompanyEndpoint.findCompanyById(em, jsonFriendlyOSType.getOsTypeCompanyID());
                if (company != null) {
                    if (entity.getCompany() != null)
                        entity.getCompany().getOsTypes().remove(entity);
                    entity.setCompany(company);
                    company.getOsTypes().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create OSType. Reason : provided Company ID " + jsonFriendlyOSType.getOsTypeCompanyID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if(jsonFriendlyOSType.getOsTypeOSInstancesID() != null) {
                if (!jsonFriendlyOSType.getOsTypeOSInstancesID().isEmpty()) {
                    for (OSInstance osInstance : entity.getOsInstances()) {
                        if (!jsonFriendlyOSType.getOsTypeOSInstancesID().contains(osInstance.getId())) {
                            entity.getOsInstances().remove(osInstance);
                            osInstance.setOsType(null);
                        }
                    }
                    for (Long osiId : jsonFriendlyOSType.getOsTypeOSInstancesID()) {
                        OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiId);
                        if (osInstance != null) {
                            if (!entity.getOsInstances().contains(osInstance)) {
                                entity.getOsInstances().add(osInstance);
                                osInstance.setOsType(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update Application. Reason : provided OS Instance ID " + osiId +" was not found.");
                            return commonRestResponse;
                        }
                    }
                } else {
                    for (OSInstance osInstance: entity.getOsInstances()) {
                        entity.getOsInstances().remove(osInstance);
                        osInstance.setOsType(entity);
                    }
                }
            }
            commonRestResponse.setDeserializedObject(entity);
        } else {
            entity = new OSType();
            entity.setNameR(jsonFriendlyOSType.getOsTypeName()).setArchitectureR(jsonFriendlyOSType.getOsTypeArchitecture());
            if (jsonFriendlyOSType.getOsTypeCompanyID() != 0) {
                Company company = CompanyEndpoint.findCompanyById(em, jsonFriendlyOSType.getOsTypeCompanyID());
                if (company != null) {
                    if (entity.getCompany() != null)
                        entity.getCompany().getOsTypes().remove(entity);
                    entity.setCompany(company);
                    company.getOsTypes().add(entity);
                } else {
                    commonRestResponse.setErrorMessage("Fail to create OSType. Reason : provided Company ID " + jsonFriendlyOSType.getOsTypeCompanyID() +" was not found.");
                    return commonRestResponse;
                }
            }
            if(jsonFriendlyOSType.getOsTypeOSInstancesID() != null) {
                if (!jsonFriendlyOSType.getOsTypeOSInstancesID().isEmpty()) {
                    for (Long osiId : jsonFriendlyOSType.getOsTypeOSInstancesID()) {
                        OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiId);
                        if (osInstance != null) {
                            if (!entity.getOsInstances().contains(osInstance)) {
                                entity.getOsInstances().add(osInstance);
                                osInstance.setOsType(entity);
                            }
                        } else {
                            commonRestResponse.setErrorMessage("Fail to update OSType. Reason : provided OS Instance ID " + osiId + " was not found.");
                            return commonRestResponse;
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
    public Response displayOSType(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get os type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("sysadmin") || subject.hasRole("sysreviewer") || subject.isPermitted("dirComITiSysOst:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            OSType entity = findOSTypeById(em, id);
            if (entity == null) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = osTypeToJSON(entity);
            em.close();
            return ret;
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display OS Types. Contact your administrator.").build();
        }
    }

    @GET
    public Response displayAllOSTypes() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get os types", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("sysadmin") || subject.hasRole("sysreviewer") || subject.isPermitted("dirComITiSysOst:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            final HashSet<OSType> results = new HashSet(em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances ORDER BY o.id", OSType.class).getResultList());

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                OSTypeJSON.manyOSTypes2JSON(results, outStream);
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display OS Types. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/get")
    public Response getOSType(@QueryParam("name")String name, @QueryParam("id")long id) {
        if (id!=0) {
            return displayOSType(id);
        } else if (name != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] get os type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name});
            if (subject.hasRole("sysadmin") || subject.hasRole("sysreviewer") || subject.isPermitted("dirComITiSysOst:display") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeByName(em, name);
                if (entity == null) {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }

                Response ret = osTypeToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/create")
    public Response createOSType(@QueryParam("name")String name, @QueryParam("architecture")String arc) {
        if (name != null && arc != null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] create os type : ({},{})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), name, arc});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOst:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeByName(em, name);
                if (entity == null) {
                    entity = new OSType().setNameR(name).setArchitectureR(arc);
                    try {
                        em.getTransaction().begin();
                        em.persist(entity);
                        em.flush();
                        em.getTransaction().commit();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating OS Type " + entity.getName() + " : " + t.getMessage()).build();
                    }
                }

                Response ret = osTypeToJSON(entity);
                em.close();
                return ret;
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: name and/or architecture are not defined. You must define one of these parameters.").build();
        }
    }

    @POST
    public Response postOSType(@QueryParam("payload") String payload) throws IOException {

        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] create/update OSType : ({})", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), payload});
        if (subject.hasRole("orgadmin") || subject.isPermitted("dirComITiSysOst:create") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone")) {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            JSONFriendlyOSType jsonFriendlyOSType = OSTypeJSON.JSON2OSType(payload);
            CommonRestResponse commonRestResponse = jsonFriendlyToHibernateFriendly(em, jsonFriendlyOSType);
            OSType entity = (OSType) commonRestResponse.getDeserializedObject();
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
                    Response ret = osTypeToJSON(entity);
                    em.close();
                    return ret;
                } catch (Throwable t) {
                    if (em.getTransaction().isActive())
                        em.getTransaction().rollback();
                    em.close();
                    return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while creating osType " + payload + " : " + t.getMessage()).build();
                }
            } else{
                em.close();
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(commonRestResponse.getErrorMessage()).build();
            }
        } else {
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to create ostypes. Contact your administrator.").build();
        }
    }

    @GET
    @Path("/delete")
    public Response deleteOSType(@QueryParam("id")Long id) {
        if (id!=0) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] delete os type : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOst:delete") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        for (OSInstance osInstance : entity.getOsInstances())
                            osInstance.setOsType(null);
                        if (entity.getCompany()!=null)
                            entity.getCompany().getOsTypes().remove(entity);
                        em.remove(entity);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("OS Type " + id + " has been successfully deleted").build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while deleting OS Type " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to delete OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id is not defined. You must define one of this parameter.").build();
        }
    }

    @GET
    @Path("/update/name")
    public Response updateOSTypeName(@QueryParam("id")Long id, @QueryParam("name")String name) {
        if (id!=0 && name!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os type {} name : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, name});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOst:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setName(name);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("OS Type " + id + " has been successfully update with name " + name).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS Type " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or name are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/update/architecture")
    public Response updateOSTypeArchitecture(@QueryParam("id")Long id, @QueryParam("architecture")String arc) {
        if (id!=0 && arc!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os type {} architecture: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, arc});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOst:update") ||
                        subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeById(em, id);
                if (entity!=null) {
                    try {
                        em.getTransaction().begin();
                        entity.setArchitecture(arc);
                        em.flush();
                        em.getTransaction().commit();
                        em.close();
                        return Response.status(Status.OK).entity("OS Type " + id + " has been successfully update with architecture " + arc).build();
                    } catch (Throwable t) {
                        if(em.getTransaction().isActive())
                            em.getTransaction().rollback();
                        em.close();
                        return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS Type " + entity.getName() + " : " + t.getMessage()).build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or arc are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/update/company")
    public Response updateOSTypeCompany(@QueryParam("id")Long id, @QueryParam("companyID") Long companyID) {
        if (id!=0 && companyID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os type {} company: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, companyID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOst:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeById(em, id);
                if (entity!=null) {
                    Company company = CompanyEndpoint.findCompanyById(em, companyID);
                    if (company!=null) {
                        try {
                            em.getTransaction().begin();
                            if (entity.getCompany()!=null && !entity.getCompany().equals(company))
                                entity.getCompany().getOsTypes().remove(entity);
                            entity.setCompany(company);
                            company.getOsTypes().add(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS Type " + id + " has been successfully update with company " + companyID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS Type " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("Company " + companyID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS Type " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or companyID are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/add")
    public Response updateOSTypeAddOSInstance(@QueryParam("id")Long id, @QueryParam("osiID") Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os type {} by adding OS instance: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOst:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeById(em, id);
                if (entity!=null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsInstances().add(osInstance);
                            if (osInstance.getOsType()!=null && !osInstance.getOsType().equals(entity))
                                osInstance.getOsType().getOsInstances().remove(osInstance);
                            osInstance.setOsType(entity);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS Type " + id + " has been successfully update by adding os instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS Type " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS Type " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or osiID are not defined. You must define one of these parameters.").build();
        }
    }

    @GET
    @Path("/update/osinstances/delete")
    public Response updateOSTypeDeleteOSInstance(@QueryParam("id")Long id, @QueryParam("osiID") Long osiID) {
        if (id!=0 && osiID!=null) {
            Subject subject = SecurityUtils.getSubject();
            log.debug("[{}-{}] update os type {} by deleting OS instance: {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id, osiID});
            if (subject.hasRole("sysadmin") || subject.isPermitted("dirComITiSysOst:update") ||
                subject.hasRole("Jedi") || subject.isPermitted("universe:zeone"))
            {
                em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
                OSType entity = findOSTypeById(em, id);
                if (entity!=null) {
                    OSInstance osInstance = OSInstanceEndpoint.findOSInstanceById(em, osiID);
                    if (osInstance!=null) {
                        try {
                            em.getTransaction().begin();
                            entity.getOsInstances().remove(osInstance);
                            if (osInstance.getOsType()!=null && osInstance.getOsType().equals(entity))
                                osInstance.setOsType(null);
                            em.flush();
                            em.getTransaction().commit();
                            em.close();
                            return Response.status(Status.OK).entity("OS Type " + id + " has been successfully update by deleting os instance " + osiID).build();
                        } catch (Throwable t) {
                            if(em.getTransaction().isActive())
                                em.getTransaction().rollback();
                            em.close();
                            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Throwable raised while updating OS Type " + entity.getName() + " : " + t.getMessage()).build();
                        }
                    } else {
                        em.close();
                        return Response.status(Status.NOT_FOUND).entity("OS instance " + osiID + " not found.").build();
                    }
                } else {
                    em.close();
                    return Response.status(Status.NOT_FOUND).entity("OS Type " + id + " not found.").build();
                }
            } else {
                return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to update OS Types. Contact your administrator.").build();
            }
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and/or osiID are not defined. You must define one of these parameters.").build();
        }
    }
}