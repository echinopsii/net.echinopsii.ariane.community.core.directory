package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.commons.model.technical.network.Datacenter;

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
@Path("/datacenters")
public class DatacenterEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Datacenter entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(DatacenterEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Datacenter entity = em.find(Datacenter.class, id);
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
      TypedQuery<Datacenter> findByIdQuery = em.createQuery("SELECT DISTINCT d FROM Datacenter d LEFT JOIN FETCH d.lans LEFT JOIN FETCH d.multicastAreas WHERE d.id = :entityId ORDER BY d.id", Datacenter.class);
      findByIdQuery.setParameter("entityId", id);
      Datacenter entity;
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
   public List<Datacenter> listAll()
   {
      final List<Datacenter> results = em.createQuery("SELECT DISTINCT d FROM Datacenter d LEFT JOIN FETCH d.lans LEFT JOIN FETCH d.multicastAreas ORDER BY d.id", Datacenter.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Datacenter entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}