package gr.aueb.mscis.softeng.team6.delivery.service;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;

import gr.aueb.mscis.softeng.team6.delivery.domain.Area;
import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service that handles statistics.
 *
 * @since 0.1.0
 * @version 0.1.1
 */
public class StatisticsService extends BaseService {
  public StatisticsService(EntityManager em) {
    super(em);
  }

  /**
   * Find the most frequent clients of a store during a certain time period.
   *
   * @param store a {@link Store} object.
   * @param start the period's start date.
   * @param end the period's end date.
   * @param max the maximum number of clients returned.
   * @return a list of clients sorted by the number of orders.
   */
  public List<Client> findFrequentClients(
      Store store, LocalDateTime start, LocalDateTime end, int max) {
    return em.createNamedQuery("findFrequentClients", Client.class)
        .setParameter("store", store)
        .setParameter("start", start)
        .setParameter("end", end)
        .setMaxResults(max)
        .getResultList();
  }

  /**
   * Calculate the average delivery time of a store for a certain area.
   *
   * @param store a {@link Store} object.
   * @param area an {@link Area} object.
   * @return the average delivery time in minutes.
   */
  public Long getAverageDeliveryTime(Store store, Area area) {
    var result =
        em.createNamedQuery("getAverageDeliveryTime", BigDecimal.class)
            .setParameter("store", store.getId())
            .setParameter("area", area.getZipCode())
            .getSingleResult();
    return result == null ? null : result.longValue();
  }

  /**
   * Find the store's rush hours for a given week.
   *
   * @param store a {@link Store} object.
   * @param week the week to check.
   * @param limit the minimum number of orders.
   * @return a list of rush hours.
   */
  public List<Integer> getRushHours(Store store, LocalDateTime week, int limit) {
    return em.createNamedQuery("getRushHours", Integer.class)
        .setParameter("store", store.getId())
        .setParameter("week", week)
        .setParameter("limit", limit)
        .getResultList();
  }

  /**
   * Truncate a date to the first day of the week.
   *
   * @param date the original date.
   * @return the truncated date.
   */
  public static LocalDateTime truncateToWeek(LocalDateTime date) {
    return date.with(DAY_OF_WEEK, 1);
  }
}
