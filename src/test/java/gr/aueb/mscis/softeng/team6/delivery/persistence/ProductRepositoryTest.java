package gr.aueb.mscis.softeng.team6.delivery.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ProductRepositoryTest {
  @Inject ProductRepository repository;

  @Test
  void testListNames() {
    var products =
        List.of(
            "Cheese Burger",
            "Mexican Pizza",
            "Pizza Margarita",
            "Special Burger",
            "Πίτα Γύρο Κοτόπουλο",
            "Πίτα Γύρο Χοιρινό");
    assertThat(repository.listNames()).hasSameElementsAs(products);
  }
}
