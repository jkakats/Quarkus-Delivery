package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.OrderProduct;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.persistence.OrderRepository;
import java.time.LocalDateTime;
import java.util.Set;
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
   * Submit an order.
   *
   * @param client the client of the order.
   * @param store the store of the order.
   * @param products the products of the order.
   */
  @Transactional
  public Order submitOrder(Client client, Store store, Set<OrderProduct> products) {
    var order = new Order().setClient(client).setStore(store);
    repository.persistAndFlush(order);
    // NOTE: we have to manually set the order and persist the
    //  products afterwards because Hibernate is a great ORM :)
    products.forEach(p -> p.setOrder(order));
    repository.persist(order.setProducts(products));
    return order;
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
    var message = CONFIRM_MESSAGE.formatted(order.getUuid(), order.getCost(), waitingTime);
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
