package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * A mapper for the {@link Order} entity.
 *
 * @since 1.0.0
 */
@Mapper(uses = {ClientMapper.class, ProductMapper.class, StoreMapper.class})
public interface OrderMapper {
  OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

  Order deserialize(OrderDto orderDto);

  OrderDto serialize(Order order);

  @AfterMapping
  default void setForeignKeys(@MappingTarget Order order) {
    order.getReview().setOrder(order);
    order.getProducts().forEach(p -> p.setOrder(order));
  }
}
