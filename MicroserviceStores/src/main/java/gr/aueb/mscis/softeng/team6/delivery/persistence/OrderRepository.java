package gr.aueb.mscis.softeng.team6.delivery.persistence;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;

/**
 * Repository for the {@link Order} entity.
 *
 * @since 1.0.0
 */
@ApplicationScoped
public class OrderRepository implements PanacheRepositoryBase<Order, UUID> {}
