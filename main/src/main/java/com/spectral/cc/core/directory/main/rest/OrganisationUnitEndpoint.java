package com.spectral.cc.core.directory.main.rest;

import com.spectral.cc.core.directory.commons.model.organisational.OrganisationUnit;

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
@Path("/organisationunits")
public class OrganisationUnitEndpoint
{
   @PersistenceContext(unitName = "cc-directory")
   private EntityManager em;

   @POST
   @Consumes("application/json")
   public Response create(OrganisationUnit entity)
   {
      em.persist(entity);
      return Response.created(UriBuilder.fromResource(OrganisationUnitEndpoint.class).path(String.valueOf(entity.getId())).build()).build();
   }

   @DELETE
   @Path("/{id:[0-9][0-9]*}")
   public Response deleteById(@PathParam("id") Long id)
   {
      OrganisationUnit entity = em.find(OrganisationUnit.class, id);
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
      TypedQuery<OrganisationUnit> findByIdQuery = em.createQuery("SELECT DISTINCT o FROM OrganisationUnit o LEFT JOIN FETCH o.applications WHERE o.id = :entityId ORDER BY o.id", OrganisationUnit.class);
      findByIdQuery.setParameter("entityId", id);
      OrganisationUnit entity;
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
   public List<OrganisationUnit> listAll()
   {
      final List<OrganisationUnit> results = em.createQuery("SELECT DISTINCT o FROM OrganisationUnit o LEFT JOIN FETCH o.applications ORDER BY o.id", OrganisationUnit.class).getResultList();
      return results;
   }

   @PUT
   @Path("/{id:[0-9][0-9]*}")
   @Consumes("application/json")
   public Response update(OrganisationUnit entity)
   {
      entity = em.merge(entity);
      return Response.noContent().build();
   }
}