package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Service that handles orders.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@RequestScoped
public class OrderService {
  /** The confirmation message format. */
  static final String CONFIRM_MESSAGE =
      "Order ID: %s%nTotal cost: %.2f%nEstimated waiting time: %d minutes";

  @Inject protected OrderRepository repository;
  @Inject protected MessageProvider messageProvider;

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
   * Submit an order.
   *
   * @param client the client of the order.
   * @param store the store of the order.
   * @param order the order to be submitted.
   */
  @Transactional
  public void submitOrder(Client client, Store store, Order order) {
    repository.persist(order.setClient(client).setStore(store));
  }

  /**
   * Send an SMS confirmation for an order.
   *
   * @param order the order to be confirmed.
   * @param waitingTime the estimated waiting time.
   */
  @Transactional
  public void confirmOrder(Order order, Long waitingTime) {
    repository.persistAndFlush(order.setConfirmed(true).setEstimatedWait(waitingTime));
    var message = String.format(CONFIRM_MESSAGE, order.getUuid(), order.getCost(), waitingTime);
    messageProvider.sendMessage(order.getClient(), message);
  }

  /**
   * Update order details on delivery.
   *
   * @param order the original {@link Order} object.
   */
  @Transactional
  public void deliverOrder(Order order) {
    repository.persist(order.setDelivered(true).setDeliveredTime(LocalDateTime.now()));
  }
}
