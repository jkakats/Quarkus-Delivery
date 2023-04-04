package gr.aueb.mscis.softeng.team6.delivery.service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Message provider stub.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
public class MessageProviderStub implements MessageProvider {
  /** The stored message. */
  private String message;

  protected String getMessage() {
    return message;
  }

  /**
   * {@inheritDoc}
   *
   * @implNote This stores the {@link #message} without actually sending an SMS to the client.
   */
  @Override
  public void sendMessage(UUID client_id, UUID uuid, BigDecimal cost, Long wait) {
    message = CONFIRM_MESSAGE.formatted(uuid, cost, wait);
  }
}
