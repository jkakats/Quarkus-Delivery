package gr.aueb.mscis.softeng.team6.delivery.service;


import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@ApplicationScoped
@Path("/products")
@RegisterRestClient
public interface ProductService {

  @GET
  List<ProductDto> getProduct(@QueryParam("product_id") List<Long> product_ids);

  @GET
  @Path("/check")
  Boolean getProductCheck(@QueryParam("product_id") List<Long> product_ids);
}
