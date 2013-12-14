package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.main.model.organisational.Environment;

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
@Path("/environments")
public class EnvironmentEndpoint
{
   @PersistenceContext(unitName = "cc-directories-main")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Environment entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(EnvironmentEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Environment entity = em.find(Environment.class, id);
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
      TypedQuery<Environment> findByIdQuery = em.createQuery("SELECT DISTINCT e FROM Environment e LEFT JOIN FETCH e.systems WHERE e.id = :entityId ORDER BY e.id", Environment.class);
      findByIdQuery.setParameter("entityId", id);
      Environment entity;
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
   public List<Environment> listAll()
   {
      final List<Environment> results = em.createQuery("SELECT DISTINCT e FROM Environment e LEFT JOIN FETCH e.systems ORDER BY e.id", Environment.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Environment entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}