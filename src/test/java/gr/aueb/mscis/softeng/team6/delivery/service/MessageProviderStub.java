package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;

/**
 * Message provider implementation.
 *
 * @since 0.1.0
 */
@Priority(1)
@Alternative
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
  public void sendMessage(Client client, String message) {
    this.message = message;
  }
}
