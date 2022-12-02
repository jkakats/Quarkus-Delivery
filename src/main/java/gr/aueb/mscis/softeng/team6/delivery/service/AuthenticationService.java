package gr.aueb.mscis.softeng.team6.delivery.service;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.EmailAddress;
import gr.aueb.mscis.softeng.team6.delivery.domain.Password;
import gr.aueb.mscis.softeng.team6.delivery.domain.PhoneNumber;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

/**
 * Service that handles client authentication.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@RequestScoped
public class AuthenticationService extends BaseService {
  /**
   * Register a new client.
   *
   * @param username the client's username.
   * @param password the client's password.
   * @param name the client's real name.
   * @param email the client's email address.
   * @param phone the client's phone number.
   * @return a new {@link Client} object or {@code null} on error.
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
    return persistObject(client);
  }

  /**
   * Authenticate an existing client.
   *
   * @param username the client's username.
   * @param password the client's password.
   * @return a {@link Client} object or {@code null} if the login failed.
   */
  @Transactional
  public Client loginClient(String username, String password) {
    Client client = null;
    try {
      client =
          em.createNamedQuery("findClientByUsername", Client.class)
              .setParameter("username", username)
              .getSingleResult();
      if (!(client.getPassword().verify(password))) {
        System.err.println("incorrect username or password");
        client = null;
      }
    } catch (NoResultException ex) {
      System.err.println("incorrect username or password");
    } catch (PersistenceException ex) {
      System.err.println(ex.getMessage());
    }
    return client;
  }

  @Override
  protected <T> T persistObject(T object) {
    var tx = em.getTransaction();
    try {
      tx.begin();
      em.persist(object);
      tx.commit();
      return object;
    } catch (ConstraintViolationException ex) {
      for (var cv : ex.getConstraintViolations()) {
        System.err.println(cv.getPropertyPath() + " " + cv.getMessage());
      }
      tx.rollback();
    } catch (EntityExistsException | ValidationException ex) {
      System.err.println(ex.getMessage());
      tx.rollback();
    } catch (PersistenceException ex) {
      System.err.println(ex.getCause().getMessage());
      tx.rollback();
    }
    return null;
  }
}
