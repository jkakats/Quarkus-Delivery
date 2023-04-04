package gr.aueb.mscis.softeng.team6.delivery.resource;

import static io.smallrye.jwt.algorithm.SignatureAlgorithm.ES256;
import static org.jboss.logging.Logger.getLogger;

import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.security.UnauthorizedException;
import io.smallrye.jwt.build.Jwt;
import java.io.Serializable;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

class JwtUtil {
  private static final long ONE_DAY = 86400;

  private JwtUtil() {}

  /** Check if the token belongs to the client with the given UUID. */
  static void checkClient(JsonWebToken jwt, UUID uuid) {
    if (jwt.getGroups().contains("client") && !jwt.getName().equals(uuid.toString())) {
      throw new UnauthorizedException();
    }
  }

  /** Check if the token belongs to the manager of the store with the given ID. */
  static void checkManager(JsonWebToken jwt, Long id) {
    if (jwt.getGroups().contains("manager") && !jwt.getSubject().equals(id.toString())) {
      throw new UnauthorizedException();
    }
  }

  /** Generate an API token for a client. */
  static UserToken clientToken(ClientDto client) {
    var address = client.address() == null ? "" : client.address().toString();
    var token =
      Jwt.upn(client.uuid().toString())
        .claim(Claims.email, client.email())
        .claim(Claims.full_name, client.name())
        .claim(Claims.phone_number, client.phoneNumber())
        .claim(Claims.preferred_username, client.username())
        .claim(Claims.address, address)
        .groups("client")
        .jws()
        .algorithm(ES256)
        .sign();
    return new UserToken(client.uuid(), token);
  }

  /** Generate an API token for an administrator. */
  static ApiToken adminToken(String name) {
    var token = Jwt.upn(name).groups("admin").expiresIn(ONE_DAY).jws().algorithm(ES256).sign();
    return new ApiToken(token);
  }

  /** Generate an API token for a store manager. */
  /*static ApiToken managerToken(String name, Long id) {
    var token =
        Jwt.upn(name)
            .subject(id.toString())
            .groups("manager")
            .expiresIn(ONE_DAY)
            .jws()
            .algorithm(ES256)
            .sign();
    return new ApiToken(token);
  }*/

  @ApplicationScoped
  static class StartupObserver {
    void onStart(@Observes StartupEvent ev) {
      var name = System.getProperty("admin.name", "root");
      getLogger("delivery").infof("Admin token: %s", adminToken(name).token);
    }
  }

  @Schema
  protected record ApiToken(String token) implements Serializable {}

  protected record UserToken(UUID uuid, String token) implements Serializable {}
}
