package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * A mapper for the {@link Order} entity.
 *
 * @since 1.0.0
 */
@Mapper(
    componentModel = "cdi",
    injectionStrategy = CONSTRUCTOR,
    uses = {ClientMapper.class, ProductMapper.class, StoreMapper.class})
public interface OrderMapper {
  Order deserialize(OrderDto orderDto);

  OrderDto serialize(Order order);

  @AfterMapping
  default void setForeignKeys(@MappingTarget Order order) {
    order.getReview().setOrder(order);
    order.getProducts().forEach(p -> p.setOrder(order));
  }
}
