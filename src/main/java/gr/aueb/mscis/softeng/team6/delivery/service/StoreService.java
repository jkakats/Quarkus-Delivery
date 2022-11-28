package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import jakarta.persistence.EntityManager;
import java.util.Set;

/**
 * Service that handles stores.
 *
 * @since 0.1.0
 */
public class StoreService extends BaseService {
  public StoreService(EntityManager em) {
    super(em);
  }

  /**
   * Register a new store.
   *
   * @param name the name of the store.
   * @param type the type of the store.
   * @param areas the areas served by the store.
   * @param products the products offered by the store.
   * @return a new {@link Store} object or {@code null} on error.
   */
  public Store registerStore(String name, String type, Set<Area> areas, Set<Product> products) {
    return persistObject(
        new Store().setName(name).setType(type).setProducts(products).setAreas(areas));
  }
}
