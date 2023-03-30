package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.EmailAddress;
import gr.aueb.mscis.softeng.team6.delivery.domain.Password;
import gr.aueb.mscis.softeng.team6.delivery.domain.PhoneNumber;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ClientRepository;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

/**
 * Service that handles client authentication.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@RequestScoped
public class AuthenticationService {
  @Inject protected ClientRepository repository;

  /**
   * Register a new client.
   *
   * @param username the client's username.
   * @param password the client's password.
   * @param name the client's real name.
   * @param email the client's email address.
   * @param phone the client's phone number.
   * @return a new {@link Client} object.
   */
  @Transactional
  public Client registerClient(
      String username, String password, String name, String email, String phone) {
    var client =
        new Client()
            .setName(name)
            .setUsername(username)
            .setPassword(new Password(password))
            .setEmail(new EmailAddress(email))
            .setPhone(new PhoneNumber(phone));
    repository.persistAndFlush(client);
    return client;
  }

  /**
   * Authenticate an existing client.
   *
   * @param username the client's username.
   * @param password the client's password.
   * @return a new {@link Client} object.
   * @throws ValidationException if the login failed.
   */
  @Transactional
  public Client loginClient(String username, String password) {
    var client = repository.findByUsername(username);
    if (client.isEmpty() || !client.get().getPassword().verify(password)) {
      throw new ValidationException("incorrect username or password");
    }
    return client.get();
  }
}
