package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.persistence.ProductRepository;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreServiceTest {
  private static final String TEST_NAME = "Pita Gyro";
  private static final String TEST_TYPE = "souvlakia";

  @Inject protected StoreService service;
  @Inject protected ProductRepository productRepository;

  @Test
  @TestTransaction
  void testRegisterStore() {
    var product = new Product().setName("Kalamaki").setPrice(new BigDecimal("3.50"));
    var area = new Area().setZipCode(10434).setCity("Athina").setState("Attica");
    var store = service.registerStore(TEST_NAME, TEST_TYPE, Set.of(area), List.of(product));
    assertThat(store).returns(TEST_NAME, Store::getName).returns(TEST_TYPE, Store::getType);
    assertThat(store.getAreas()).singleElement().isEqualTo(area);
    assertThat(store.getProducts()).singleElement().isEqualTo(product);
  }
}
