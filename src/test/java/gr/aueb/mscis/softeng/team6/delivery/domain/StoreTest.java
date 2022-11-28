package gr.aueb.mscis.softeng.team6.delivery.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StoreTest {
  private static final String TEST_NAME = "The Store";
  private static final String TEST_TYPE = "supermarket";

  private Store store;

  @BeforeEach
  void setUp() {
    store = new Store().setName(TEST_NAME).setType(TEST_TYPE);
  }

  @Test
  void testEquals() {
    var store2 = new Store().setName(TEST_NAME);
    assertThat(store).hasSameHashCodeAs(store2);
    store2.setName("Other Store");
    assertThat(store).isNotEqualTo(store2);
  }

  @Test
  void testToString() {
    assertThat(store).hasToString("Store{name=\"The Store\", type=\"supermarket\"}");
  }

  @Test
  void checkIfNull() {
    assertNotNull(store);
  }
}
