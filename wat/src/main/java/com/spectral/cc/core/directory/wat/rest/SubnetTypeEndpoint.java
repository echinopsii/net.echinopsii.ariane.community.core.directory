package com.spectral.cc.core.directory.wat.rest;

import com.spectral.cc.core.directory.base.model.technical.network.SubnetType;

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
@Path("/subnettypes")
public class SubnetTypeEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(SubnetType entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(SubnetTypeEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      SubnetType entity = em.find(SubnetType.class, id);
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
      TypedQuery<SubnetType> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM SubnetType l LEFT JOIN FETCH l.subnets WHERE l.id = :entityId ORDER BY l.id", SubnetType.class);
      findByIdQuery.setParameter("entityId", id);
      SubnetType entity;
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
   public List<SubnetType> listAll()
   {
      final List<SubnetType> results = em.createQuery("SELECT DISTINCT l FROM SubnetType l LEFT JOIN FETCH l.subnets ORDER BY l.id", SubnetType.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(SubnetType entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}