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
package com.spectral.cc.core.directory.wat.rest.organisational;

import com.spectral.cc.core.directory.wat.json.ToolBox;
import com.spectral.cc.core.directory.wat.json.ds.organisational.ApplicationJSON;
import com.spectral.cc.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import com.spectral.cc.core.directory.base.model.organisational.Application;
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

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayApplication(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get application : {}", new Object[]{Thread.currentThread().getId(), id, subject.getPrincipal()});
        if (subject.hasRole("ccorgadmin") || subject.hasRole("ccorgreviewer") || subject.isPermitted("ccDirComOrgApp:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            TypedQuery<Application> findByIdQuery = em.createQuery("SELECT DISTINCT a FROM Application a LEFT JOIN FETCH a.osInstances LEFT JOIN FETCH a.company WHERE a.id = :entityId ORDER BY a.id", Application.class);
            findByIdQuery.setParameter("entityId", id);
            Application entity;
            try {
                entity = findByIdQuery.getSingleResult();
            } catch (NoResultException nre) {
                em.close();
                return Response.status(Status.NOT_FOUND).build();
            }

            Response ret = null;
            String result;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            try {
                ApplicationJSON.oneApplication2JSON(entity, outStream);
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
    public Response displayAllApplications() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get applications", new Object[]{Thread.currentThread().getId(),subject.getPrincipal()});
        if (subject.hasRole("ccorgadmin") || subject.hasRole("ccorgreviewer") || subject.isPermitted("ccDirComOrgApp:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
}