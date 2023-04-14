package gr.aueb.mscis.softeng.team6.delivery.service;

import static java.time.temporal.ChronoField.DAY_OF_WEEK;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Service that handles statistics.
 *
 * @since 0.1.0
 * @version 1.0.0
 */
@RequestScoped
public class StatisticsService {
  @Inject protected EntityManager em;

  /**
   * Find the most frequent clients of a store during a certain time period.
   *
   * @param store_id object.
   * @param start the period's start date.
   * @param end the period's end date.
   * @param max the maximum number of clients returned.
   * @return a list of clients sorted by the number of orders.
   */
  @Transactional
  public List<UUID> findFrequentClients(
      long store_id, LocalDateTime start, LocalDateTime end, int max) {
    return em.createNamedQuery("findFrequentClients", UUID.class)
        .setParameter("store", store_id)
        .setParameter("start", start)
        .setParameter("end", end)
        .setMaxResults(max)
        .getResultList();
  }

  /**
   * Calculate the average delivery time of a store for a certain area.
   *
   * @param store_id object.
   * @param clientUUIDs list.
   * @return the average delivery time in minutes.
   */
  @Transactional
  public Long getAverageDeliveryTime(long store_id, List<String> clientUUIDs) {
    var result =
        em.createNamedQuery("getAverageDeliveryTime", BigDecimal.class)
            .setParameter("store", store_id)
            .setParameter("clientUUIDs", clientUUIDs)
            .getSingleResult();
    return result == null ? null : result.longValue();
  }

  /**
   * Find the store's rush hours for a given week.
   *
   * @param store_id object.
   * @param week the week to check.
   * @param limit the minimum number of orders.
   * @return a list of rush hours.
   */
  @Transactional
  public List<Integer> getRushHours(long store_id, LocalDateTime week, int limit) {
    return em.createNamedQuery("getRushHours", Integer.class)
        .setParameter("store", store_id)
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
