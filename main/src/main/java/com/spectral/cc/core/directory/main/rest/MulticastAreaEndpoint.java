package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.commons.model.technical.network.MulticastArea;

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
@Path("/multicastareas")
public class MulticastAreaEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(MulticastArea entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(MulticastAreaEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      MulticastArea entity = em.find(MulticastArea.class, id);
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
      TypedQuery<MulticastArea> findByIdQuery = em.createQuery("SELECT DISTINCT m FROM MulticastArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.datacenters WHERE m.id = :entityId ORDER BY m.id", MulticastArea.class);
      findByIdQuery.setParameter("entityId", id);
      MulticastArea entity;
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
   public List<MulticastArea> listAll()
   {
      final List<MulticastArea> results = em.createQuery("SELECT DISTINCT m FROM MulticastArea m LEFT JOIN FETCH m.subnets LEFT JOIN FETCH m.datacenters ORDER BY m.id", MulticastArea.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(MulticastArea entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}