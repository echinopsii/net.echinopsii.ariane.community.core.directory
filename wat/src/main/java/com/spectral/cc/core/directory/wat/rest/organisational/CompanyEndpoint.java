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
package com.spectral.cc.core.directory.wat.rest.organisational;

import com.spectral.cc.core.directory.base.model.organisational.Company;
import com.spectral.cc.core.directory.wat.json.ToolBox;
import com.spectral.cc.core.directory.wat.json.ds.organisational.ApplicationJSON;
import com.spectral.cc.core.directory.wat.json.ds.organisational.CompanyJSON;
import com.spectral.cc.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;

/**
 *
 */
@Path("directories/common/organisation/companies")
public class CompanyEndpoint {
    private static final Logger log = LoggerFactory.getLogger(CompanyEndpoint.class);
    private EntityManager em;

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayCompany(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get company : {},{}", new Object[]{Thread.currentThread().getId(), id, subject.getPrincipal()});
        if (subject.hasRole("ccorgadmin") || subject.hasRole("ccorgreviewer") || subject.isPermitted("ccDirComOrgCompany:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            TypedQuery<Company> findByIdQuery = em.createQuery("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.applications WHERE c.id = :entityId ORDER BY c.id", Company.class);
            findByIdQuery.setParameter("entityId", id);
            Company entity;
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
                CompanyJSON.oneCompany2JSON(entity, outStream);
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
    public Response DisplayAllCompanies() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get company : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ccorgadmin") || subject.hasRole("ccorgreviewer") || subject.isPermitted("ccDirComOrgCompany:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
}