package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import javax.enterprise.context.ApplicationScoped;

/**
 * Message provider implementation.
 *
 * @since 1.0.0
 */
@ApplicationScoped
public class MessageProviderImpl implements MessageProvider {
  @Override
  public void sendMessage(Client client, String message) {
    throw new IllegalStateException("Not implemented");
  }
}
