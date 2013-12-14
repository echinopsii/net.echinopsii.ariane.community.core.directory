package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.main.model.technical.network.Lan;

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
@Path("/lans")
public class LanEndpoint
{
   @PersistenceContext(unitName = "cc-directories-main")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Lan entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(LanEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Lan entity = em.find(Lan.class, id);
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
      TypedQuery<Lan> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM Lan l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.datacenters LEFT JOIN FETCH l.type LEFT JOIN FETCH l.marea WHERE l.id = :entityId ORDER BY l.id", Lan.class);
      findByIdQuery.setParameter("entityId", id);
      Lan entity;
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
   public List<Lan> listAll()
   {
      final List<Lan> results = em.createQuery("SELECT DISTINCT l FROM Lan l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.datacenters LEFT JOIN FETCH l.type LEFT JOIN FETCH l.marea ORDER BY l.id", Lan.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Lan entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}