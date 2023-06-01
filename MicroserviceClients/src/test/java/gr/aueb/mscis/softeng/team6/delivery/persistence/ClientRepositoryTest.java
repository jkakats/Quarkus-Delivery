package gr.aueb.mscis.softeng.team6.delivery.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
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

  @Test
  void testFindByZipcode() {
    List<String> result = repository.findByZipcode(10434);
    assertEquals(3, result.size());
    assertEquals("4948b178-f325-4f5f-b8ea-0b4d60cd006c", result.get(0));
  }
}
