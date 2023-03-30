package gr.aueb.mscis.softeng.team6.delivery.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreRepositoryTest {
  @Inject protected StoreRepository repository;

  @Test
  void findByAreaTest() {
    var area = new Area().setCity("Αθήνα").setState("Αττική").setZipCode(10434);
    var products = List.of("Cheese Burger", "Special Burger");
    var stores = repository.findByArea(area, products);
    assertThat(stores)
        .singleElement()
        .returns("Goody's Burger House", Store::getName)
        .returns("Burgers", Store::getType);
  }
}
