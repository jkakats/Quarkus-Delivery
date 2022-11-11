package gr.aueb.mscis.softeng.team6.delivery.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EntityManagerUtilTest {

  @Test
  void testGetManager() {
    var manager = EntityManagerUtil.getManager();
    assertThat(manager.isOpen()).isTrue();
  }

  @Test
  void testRunTransaction() {
    EntityManagerUtil.runTransaction(
        em -> {
          var query = em.createNativeQuery("SHOW TABLES");
          assertThat(query.getResultList()).isEmpty();
        });
  }
}
