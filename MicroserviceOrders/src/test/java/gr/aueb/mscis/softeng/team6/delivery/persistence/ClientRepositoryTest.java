package gr.aueb.mscis.softeng.team6.delivery.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ClientRepositoryTest {
  @Inject protected ClientRepository repository;

  @Test
  void testFindByUsername() {
    assertThat(repository.findByUsername("johndoe2")).isPresent();
    assertThat(repository.findByUsername("johndoe3")).isNotPresent();
  }
}
