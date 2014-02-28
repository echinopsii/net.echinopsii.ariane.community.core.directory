package com.spectral.cc.core.directory.wat.rest;

import com.spectral.cc.core.directory.base.model.technical.network.Subnet;

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
@Path("/subnets")
public class SubnetEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Subnet entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(SubnetEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Subnet entity = em.find(Subnet.class, id);
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
      TypedQuery<Subnet> findByIdQuery = em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.datacenters LEFT JOIN FETCH l.type LEFT JOIN FETCH l.marea WHERE l.id = :entityId ORDER BY l.id", Subnet.class);
      findByIdQuery.setParameter("entityId", id);
      Subnet entity;
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
   public List<Subnet> listAll()
   {
      final List<Subnet> results = em.createQuery("SELECT DISTINCT l FROM Subnet l LEFT JOIN FETCH l.osInstances LEFT JOIN FETCH l.datacenters LEFT JOIN FETCH l.type LEFT JOIN FETCH l.marea ORDER BY l.id", Subnet.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Subnet entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}