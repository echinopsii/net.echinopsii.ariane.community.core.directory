package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.commons.model.technical.system.OSInstance;

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
@Path("/osinstances")
public class OSInstanceEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(OSInstance entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(OSInstanceEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      OSInstance entity = em.find(OSInstance.class, id);
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
      TypedQuery<OSInstance> findByIdQuery = em.createQuery("SELECT DISTINCT o FROM OSInstance o LEFT JOIN FETCH o.networkLan LEFT JOIN FETCH o.embeddingOSInstance LEFT JOIN FETCH o.embeddedOSInstances LEFT JOIN FETCH o.osType LEFT JOIN FETCH o.applications LEFT JOIN FETCH o.teams LEFT JOIN FETCH o.environments WHERE o.id = :entityId ORDER BY o.id", OSInstance.class);
      findByIdQuery.setParameter("entityId", id);
      OSInstance entity;
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
   public List<OSInstance> listAll()
   {
      final List<OSInstance> results = em.createQuery("SELECT DISTINCT o FROM OSInstance o LEFT JOIN FETCH o.networkLan LEFT JOIN FETCH o.embeddingOSInstance LEFT JOIN FETCH o.embeddedOSInstances LEFT JOIN FETCH o.osType LEFT JOIN FETCH o.applications LEFT JOIN FETCH o.teams LEFT JOIN FETCH o.environments ORDER BY o.id", OSInstance.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(OSInstance entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}