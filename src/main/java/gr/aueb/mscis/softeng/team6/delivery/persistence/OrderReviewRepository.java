package gr.aueb.mscis.softeng.team6.delivery.persistence;

import gr.aueb.mscis.softeng.team6.delivery.domain.OrderReview;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import javax.enterprise.context.ApplicationScoped;

/**
 * Repository for the {@link OrderReview} entity.
 *
 * @since 1.0.0
 */
@ApplicationScoped
public class OrderReviewRepository implements PanacheRepositoryBase<OrderReview, Long> {}
