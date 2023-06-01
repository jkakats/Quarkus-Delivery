package gr.aueb.mscis.softeng.team6.delivery.persistence;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Parameters;
import java.util.ArrayList;
import java.util.List;
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

  public List<String> findByZipcode(int zipcode) {
    List<Client> clients =
        find(
                "select c from Client c where address_zip_code like :zipcode",
                Parameters.with("zipcode", zipcode).map())
            .list();
    List<String> clients_uuids = new ArrayList<>();
    for (Client client : clients) {
      clients_uuids.add(client.getUuid().toString());
    }
    return clients_uuids;
  }
}
