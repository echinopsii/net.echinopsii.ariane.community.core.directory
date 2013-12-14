package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.main.model.technical.network.LanType;

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
@Path("/lantypes")
public class LanTypeEndpoint
{
   @PersistenceContext(unitName = "cc-directories-main")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(LanType entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(LanTypeEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      LanType entity = em.find(LanType.class, id);
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
      TypedQuery<LanType> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM LanType l LEFT JOIN FETCH l.lans WHERE l.id = :entityId ORDER BY l.id", LanType.class);
      findByIdQuery.setParameter("entityId", id);
      LanType entity;
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
   public List<LanType> listAll()
   {
      final List<LanType> results = em.createQuery("SELECT DISTINCT l FROM LanType l LEFT JOIN FETCH l.lans ORDER BY l.id", LanType.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(LanType entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}