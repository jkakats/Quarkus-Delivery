package gr.aueb.mscis.softeng.team6.delivery.service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Text message provider.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
public interface MessageProvider {
  /** The confirmation message format. */
  String CONFIRM_MESSAGE = "Order ID: %s%nTotal cost: %.2f%nEstimated waiting time: %d minutes";

  /**
   * Send a text message to a client.
   *
   * @param client_uuid the client that will receive the message.
   * @param uuid the UUID of the order.
   * @param cost the total cost of the order.
   * @param wait the estimated waiting time.
   */
  void sendMessage(UUID client_uuid, UUID uuid, BigDecimal cost, Long wait);
}
