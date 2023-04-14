package gr.aueb.mscis.softeng.team6.delivery.service;


import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@Path("/products")
@RegisterRestClient
public interface ProductService {

  @GET
  @Path("/{product_id}")
  ProductDto getProduct(@PathParam("product_id") long product_id);

  @GET
  @Path("/check")
  Boolean getProductCheck();
}
