package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.commons.model.organisational.Company;

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
@Path("/companys")
public class CompanyEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(Company entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(CompanyEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      Company entity = em.find(Company.class, id);
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
      TypedQuery<Company> findByIdQuery = em.createQuery("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.applications WHERE c.id = :entityId ORDER BY c.id", Company.class);
      findByIdQuery.setParameter("entityId", id);
      Company entity;
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
   public List<Company> listAll()
   {
      final List<Company> results = em.createQuery("SELECT DISTINCT c FROM Company c LEFT JOIN FETCH c.applications ORDER BY c.id", Company.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(Company entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}