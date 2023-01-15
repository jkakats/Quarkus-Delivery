package gr.aueb.mscis.softeng.team6.delivery.persistence;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import java.util.HashMap;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;

/**
 * Repository for the {@link Store} entity.
 *
 * @since 1.0.0
 */
@ApplicationScoped
public class StoreRepository implements PanacheRepositoryBase<Store, Long> {
  /**
   * Find stores in the area that have all the requested products.
   *
   * @param area an {@link Area} object.
   * @param products a list of product names
   * @return a list of {@link Store} objects.
   */
  public List<Store> findByArea(Area area, List<String> products) {
    var params = new HashMap<String, Object>(3);
    params.put("area", area.getZipCode());
    params.put("products", products);
    params.put("count", (long) products.size());
    // language=HQL
    var query =
        """
        from Store s
        join s.areas a
        where a.zipCode = :area
          and :count = (
            select
              count(distinct p.name)
            from Product p
            where p.name in :products
              and p.store.id = s.id)
        """;
    return list(query, params);
  }
}
