package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;

/**
 * Text message provider.
 *
 * @since 0.1.0
 */
public interface MessageProvider {
  /**
   * Send a text message to a client.
   *
   * @param client the client that will receive the message.
   * @param message the text message to be sent.
   */
  void sendMessage(Client client, String message);
}
