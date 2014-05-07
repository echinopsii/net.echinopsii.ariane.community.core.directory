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
package com.spectral.cc.core.directory.wat.rest.technical.system;

import com.spectral.cc.core.directory.base.model.technical.system.OSType;
import com.spectral.cc.core.directory.wat.json.ToolBox;
import com.spectral.cc.core.directory.wat.json.ds.technical.system.OSTypeJSON;
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
@Path("/directories/common/infrastructure/system/ostypes")
public class OSTypeEndpoint {
    private static final Logger log = LoggerFactory.getLogger(OSTypeEndpoint.class);
    private EntityManager em;

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayOSType(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get os instance : {}", new Object[]{Thread.currentThread().getId(), id, subject.getPrincipal()});
        if (subject.hasRole("ccsysadmin") || subject.hasRole("ccsysreviewer") || subject.isPermitted("ccDirComITiSysOst:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            TypedQuery<OSType> findByIdQuery = em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances WHERE o.id = :entityId ORDER BY o.id", OSType.class);
            findByIdQuery.setParameter("entityId", id);
            OSType entity;
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
                OSTypeJSON.oneOSType2JSON(entity, outStream);
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
    public Response displayAllOSTypes() {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get os instance", new Object[]{Thread.currentThread().getId(), subject.getPrincipal()});
        if (subject.hasRole("ccsysadmin") || subject.hasRole("ccsysreviewer") || subject.isPermitted("ccDirComITiSysOst:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
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
            return Response.status(Status.UNAUTHORIZED).entity("You're not authorized to display OS Instances. Contact your administrator.").build();
        }
    }

}