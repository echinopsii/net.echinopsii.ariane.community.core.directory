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
package com.spectral.cc.core.directory.wat.rest.technical.network;

import com.spectral.cc.core.directory.base.model.technical.network.MulticastArea;
import com.spectral.cc.core.directory.wat.json.ToolBox;
import com.spectral.cc.core.directory.wat.json.ds.technical.network.MulticastAreaJSON;
import com.spectral.cc.core.directory.wat.plugin.DirectoryJPAProviderConsumer;
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

    @GET
    @Path("/{id:[0-9][0-9]*}")
    public Response displayMulticastArea(@PathParam("id") Long id) {
        Subject subject = SecurityUtils.getSubject();
        log.debug("[{}-{}] get multicast area : {}", new Object[]{Thread.currentThread().getId(), subject.getPrincipal(), id});
        if (subject.hasRole("ccntwadmin") || subject.hasRole("ccntwreviewer") || subject.isPermitted("ccDirComITiNtwMarea:display") ||
            subject.hasRole("Jedi") || subject.isPermitted("ccuniverse:zeone"))
        {
            em = DirectoryJPAProviderConsumer.getInstance().getDirectoryJpaProvider().createEM();
            TypedQuery<MulticastArea> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM MulticastArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.datacenters WHERE m.id = :entityId ORDER BY m.id", MulticastArea.class);
            findByIdQuery.setParameter("entityId", id);
            MulticastArea entity;
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
                MulticastAreaJSON.oneMulticastArea2JSON(entity, outStream);
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
                TypedQuery<MulticastArea> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM MulticastArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.datacenters WHERE m.name = :entityName ORDER BY m.name", MulticastArea.class);
                findByIdQuery.setParameter("entityName", name);
                MulticastArea entity;
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
                    MulticastAreaJSON.oneMulticastArea2JSON(entity, outStream);
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
        } else {
            return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Request error: id and name are not defined. You must define one of these parameters.").build();
        }
    }
}