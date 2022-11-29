package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

/**
 * Service that handles orders.
 *
 * @since 0.1.0
 * @version 0.1.1
 */
public class OrderService extends BaseService {
  /** The confirmation message format. */
  static final String CONFIRM_MESSAGE =
      "Order ID: %s%nTotal cost: %.2f%nEstimated waiting time: %d minutes";

  private MessageProvider messageProvider;

  public OrderService(EntityManager em) {
    super(em);
  }

  public void setMessageProvider(MessageProvider messageProvider) {
    this.messageProvider = messageProvider;
  }

  /**
   * Get all the products in the catalogue.
   *
   * @return a list of {@link Product} objects.
   */
  public List<Product> getAllProducts() {
    return em.createNamedQuery("getAllProducts", Product.class).getResultList();
  }

  /**
   * Choose products for an order.
   *
   * @param products a list of products.
   * @param quantities the corresponding quantities.
   * @return a new {@link Order} object.
   * @throws IllegalArgumentException if the size of the parameters differs.
   */
  public Order chooseProducts(List<Product> products, int[] quantities)
      throws IllegalArgumentException {
    var size = products.size();
    if (size != quantities.length) {
      throw new IllegalArgumentException("Products and quantities must have the same size");
    }
    var order = new Order().setProducts(new HashSet<>(size));
    for (int i = 0; i < size; ++i) {
      if (quantities[i] > 0) {
        order.addProduct(products.get(i), quantities[i]);
      }
    }
    return order;
  }

  /**
   * Find stores near the client that can fulfill the order.
   *
   * @param client a {@link Client} object.
   * @param order an {@link Order} object.
   * @return a list of {@link Store} objects.
   */
  public List<Store> findNearbyStores(Client client, Order order) {
    var products = order.getProducts().stream().map(o -> o.getProduct().getId()).toList();
    return em.createNamedQuery("findNearbyStores", Store.class)
        .setParameter("area", client.getAddress().getArea().getZipCode())
        .setParameter("count", products.size())
        .setParameter("products", products)
        .getResultList();
  }

  /**
   * Submit an order.
   *
   * @param client the client of the order.
   * @param store the store of the order.
   * @param order the order to be submitted.
   * @return the updated {@link Order} object or {@code null} on error.
   */
  public Order submitOrder(Client client, Store store, Order order) {
    return persistObject(order.setClient(client).setStore(store));
  }

  /**
   * Send an SMS confirmation for an order.
   *
   * @param order the order to be confirmed.
   * @param waitingTime the estimated waiting time.
   * @return the updated {@link Order} object or {@code null} on error.
   */
  public Order confirmOrder(Order order, Long waitingTime) {
    var result = persistObject(order.setConfirmed(true).setEstimatedWait(waitingTime));
    if (result != null) {
      var message = String.format(CONFIRM_MESSAGE, order.getUuid(), order.getCost(), waitingTime);
      messageProvider.sendMessage(order.getClient(), message);
    }
    return result;
  }

  /**
   * Update order details on delivery.
   *
   * @param order the original {@link Order} object.
   * @return the updated {@link Order} object or {@code null} on error.
   */
  public Order deliverOrder(Order order) {
    return persistObject(order.setDelivered(true).setDeliveredTime(LocalDateTime.now()));
  }
}
