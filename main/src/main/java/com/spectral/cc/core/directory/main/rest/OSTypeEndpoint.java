package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.commons.model.technical.system.OSType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import java.util.List;

/**
 * 
 */
@Path("/ostypes")
public class OSTypeEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(OSType entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(OSTypeEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      OSType entity = em.find(OSType.class, id);
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      em.remove(entity);
      return Response.noContent().build();
   }

   @GET
   @Path("/{id:[0-9][0-9]*}")
   @Produces("application/json")
   public Response findById(@PathParam("id") Long id)
   {
      TypedQuery<OSType> findByIdQuery = em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances WHERE o.id = :entityId ORDER BY o.id", OSType.class);
      findByIdQuery.setParameter("entityId", id);
      OSType entity;
      try
      {
         entity = findByIdQuery.getSingleResult();
      }
      catch (NoResultException nre)
      {
         entity = null;
      }
      if (entity == null)
      {
         return Response.status(Status.NOT_FOUND).build();
      }
      return Response.ok(entity).build();
   }

   @GET
   @Produces("application/json")
   public List<OSType> listAll()
   {
      final List<OSType> results = em.createQuery("SELECT DISTINCT o FROM OSType o LEFT JOIN FETCH o.osInstances ORDER BY o.id", OSType.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(OSType entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}