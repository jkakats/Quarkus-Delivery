package gr.aueb.mscis.softeng.team6.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.persistence.StoreRepository;
import io.quarkus.test.junit.QuarkusTest;
import java.time.LocalDateTime;
import javax.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StatisticsServiceTest {
  private Store store;

  @Inject protected StatisticsService service;
  @Inject protected StoreRepository storeRepository;

  @BeforeEach
  void setUp() {
    store = storeRepository.findById(4L);
  }

  @Test
  void testFindFrequentClients() {
    var start = LocalDateTime.of(2022, 12, 7, 0, 0);
    var end = LocalDateTime.of(2022, 12, 11, 0, 0);
    var results = service.findFrequentClients(store, start, end, 3);
    assertThat(results).hasSize(2).first().returns("eudim", Client::getUsername);
  }

  @Test
  void testGetAverageDeliveryTime() {
    var area = new Area().setZipCode(10434);
    assertThat(service.getAverageDeliveryTime(store, area)).isEqualTo(22);
  }

  @Test
  void testGetRushHours() {
    var week = StatisticsService.truncateToWeek(LocalDateTime.of(2022, 12, 7, 0, 0));
    assertThat(service.getRushHours(store, week, 1)).first().isEqualTo(12);
  }
}
