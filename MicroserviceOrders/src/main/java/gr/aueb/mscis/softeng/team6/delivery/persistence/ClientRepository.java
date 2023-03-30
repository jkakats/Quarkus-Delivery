package gr.aueb.mscis.softeng.team6.delivery.persistence;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import java.util.Optional;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;

/**
 * Repository for the {@link Client} entity.
 *
 * @since 1.0.0
 */
@ApplicationScoped
public class ClientRepository implements PanacheRepositoryBase<Client, UUID> {
  /**
   * Find a client by their username.
   *
   * @param username the client's username
   * @return an optional {@link Client} object.
   */
  public Optional<Client> findByUsername(String username) {
    return find("username", username).singleResultOptional();
  }
}
