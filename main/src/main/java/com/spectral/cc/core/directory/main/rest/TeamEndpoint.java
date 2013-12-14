package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.main.model.organisational.Team;

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
@Path("/teams")
public class TeamEndpoint
{
   @PersistenceContext(unitName = "cc-directories-main")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Team entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(TeamEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Team entity = em.find(Team.class, id);
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
      TypedQuery<Team> findByIdQuery = em.createQuery("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.osInstances WHERE t.id = :entityId ORDER BY t.id", Team.class);
      findByIdQuery.setParameter("entityId", id);
      Team entity;
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
   public List<Team> listAll()
   {
      final List<Team> results = em.createQuery("SELECT DISTINCT t FROM Team t LEFT JOIN FETCH t.osInstances ORDER BY t.id", Team.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Team entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}