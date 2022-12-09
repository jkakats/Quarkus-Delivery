package gr.aueb.mscis.softeng.team6.delivery.persistence;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
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
   * Find stores near the client that can fulfill the order.
   *
   * @param client a {@link Client} object.
   * @param order an {@link Order} object.
   * @return a list of {@link Store} objects.
   */
  public List<Store> findNearby(Client client, Order order) {
    var products = order.getProducts().stream().map(o -> o.getProduct().getId()).toList();
    var params =
        Parameters.with("area", client.getAddress().getArea().getZipCode())
            .and("count", products.size())
            .and("products", products);
    return list("#findNearbyStores", params);
  }
}
