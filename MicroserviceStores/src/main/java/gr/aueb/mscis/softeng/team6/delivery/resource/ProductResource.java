package gr.aueb.mscis.softeng.team6.delivery.resource;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;

import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ProductRepository;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.mapper.ProductMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;

/**
 * Product resource class.
 *
 * @since 1.0.0
 */
@RequestScoped
@Path("/products")
public class ProductResource {
  @Inject protected ProductRepository repository;
  @Inject protected ProductMapper mapper;
  @Inject protected JsonWebToken jwt;

  /** Get all the products or products with specific id. */
  @GET
  @Transactional
  public Response list(@QueryParam("product_id") List<Long> productIds) {
    if (productIds == null || productIds.isEmpty()) {
      // return all products
      var products = repository.streamAll().map(mapper::serialize).toList();
      return Response.ok(products).build();
    } else {
      // return products by id
      List<ProductDto> productList = new ArrayList<>();
      for (Long id : productIds) {
        Product product = repository.findByIdOptional(id).orElse(null);
        if (product == null) {
          return Response.status(Response.Status.NOT_FOUND).build(); // return 404 if product not found
        } else {
          productList.add(mapper.serialize(product));
        }
      }
      return Response.ok(productList).build();
    }
  }




  /**
   * Get a single product.
   *
   * @param id the product's ID
   */
  @GET
  @Transactional
  @Path("{id}")
  public Response read(@PathParam("id") Long id) throws NoSuchElementException {
    var product = repository.findByIdOptional(id).orElseThrow();
    return Response.ok(mapper.serialize(product)).build();
  }

  /**
   * Add a new product.
   *
   * @param dto a store DTO
   */
  @POST
  @Transactional
  @RolesAllowed({"admin", "manager"})
  @APIResponses({
    @APIResponse(responseCode = "201", description = "Created"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response create(@Context UriInfo uriInfo, @Valid ProductDto dto)
      throws PersistenceException {
    JwtUtil.checkManager(jwt, dto.store().id());
    var product = mapper.deserialize(dto);
    // NOTE: persistAndFlush doesn't work here
    product = repository.getEntityManager().merge(product);
    repository.flush();
    var uri = uriInfo.getRequestUriBuilder().path("{id}").build(product.getId());
    return Response.created(uri).build();
  }

  /**
   * Update an existing product.
   *
   * @param id the product's ID
   * @param dto the updated product DTO
   */
  @PUT
  @Transactional
  @Path("{id}")
  @RolesAllowed({"admin", "manager"})
  @APIResponses({
    @APIResponse(responseCode = "200", description = "Updated"),
    @APIResponse(responseCode = "400", description = "Validation failed")
  })
  public Response update(@PathParam("id") Long id, @Valid ProductDto dto)
      throws NoSuchElementException, PersistenceException {
    JwtUtil.checkManager(jwt, dto.store().id());
    var product = repository.findByIdOptional(id, PESSIMISTIC_WRITE).orElseThrow();
    mapper.update(product, dto);
    repository.persistAndFlush(product);
    return Response.ok(mapper.serialize(product)).build();
  }

  /**
   * Delete the given product.
   *
   * @param id the product's ID
   */
  @DELETE
  @Transactional
  @Path("{id}")
  @RolesAllowed({"admin", "manager"})
  @APIResponse(responseCode = "204", description = "Deleted")
  public Response delete(@PathParam("id") Long id) throws NoSuchElementException {
    var product = repository.findByIdOptional(id).orElseThrow();
    JwtUtil.checkManager(jwt, product.getStore().getId());
    repository.delete(product);
    return Response.noContent().build();
  }

  /** Get all the product names. */
  @GET
  @Transactional
  @Path("catalogue")
  public Response catalogue() {
    return Response.ok(repository.listNames()).build();
  }

  /**
   * Find products by the given name.
   *
   * @param name a product name
   */
  @GET
  @Transactional
  @Path("search")
  public Response search(@QueryParam("q") @NotBlank String name) {
    Object[] params = {name};
    var products = repository.stream("name", params).map(mapper::serialize).toList();
    return Response.ok(products).build();
  }

  @GET
  @Transactional
  @Path("check")
  public Response check(@QueryParam("product_id") List<Long> productIds) throws NoSuchElementException
  {
      // check if all products exist
     boolean check=true;
      for (Long id : productIds)
      {
        Product product = repository.findById(id);
        if (product == null) {
          check=false;
          break;
        }
      }
      return Response.ok(check).build();
    }

}
