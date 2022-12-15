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
   * Show all products in the catalogue.
   *
   * @return a list of distinct product names.
   */
  public List<String> listNames() {
    // language=HQL
    var query = "select distinct p.name from Product p order by p.name";
    return find(query).project(String.class).list();
  }
}
