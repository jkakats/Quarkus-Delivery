package gr.aueb.mscis.softeng.team6.delivery.persistence;

import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

/**
 * Repository for the {@link Product} entity.
 *
 * @since 1.0.0
 */
@ApplicationScoped
public class ProductRepository implements PanacheRepositoryBase<Product, Long> {
  /**
   * Search for products in the catalogue.
   *
   * @param name a product name.
   * @return a list of {@link Product} objects.
   */
  public List<Product> search(String name) {
    var query = "from Product p join fetch p.store";
    return name == null ? list(query) : list(query + " where p.name like ?1", name);
  }
}
