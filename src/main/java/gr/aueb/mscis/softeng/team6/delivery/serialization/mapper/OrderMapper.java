package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.domain.OrderProduct;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderProductDto;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * A mapper for the {@link Order} entity.
 *
 * @since 1.0.0
 */
@Mapper(
    componentModel = "cdi",
    injectionStrategy = CONSTRUCTOR,
    uses = {
      ClientMapper.class,
      ProductMapper.class,
      StoreMapper.class,
      OrderMapper.OrderProductMapper.class
    })
public abstract class OrderMapper {
  @Mapping(target = "store.orders", ignore = true)
  @Mapping(target = "store.products", ignore = true)
  @Mapping(target = "review.order", ignore = true)
  @Mapping(target = "review.productReviews", ignore = true)
  public abstract Order deserialize(OrderDto orderDto);

  @Mapping(target = "store.products", ignore = true)
  public abstract OrderDto serialize(Order order);

  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "store.orders", ignore = true)
  @Mapping(target = "store.products", ignore = true)
  @Mapping(target = "review.order", ignore = true)
  @Mapping(target = "review.productReviews", ignore = true)
  public abstract void update(@MappingTarget Order order, OrderDto orderDto);

  @AfterMapping
  protected void setForeignKeys(@MappingTarget Order order) {
    order.getReview().setOrder(order);
    order.getProducts().forEach(p -> p.setOrder(order));
  }

  /**
   * A mapper for the {@link OrderProduct} entity.
   *
   * @since 1.0.0
   */
  @Mapper
  abstract static class OrderProductMapper {
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "review.product", ignore = true)
    @Mapping(target = "review.parent", ignore = true)
    @Mapping(target = "product.store", ignore = true)
    protected abstract OrderProduct deserialize(OrderProductDto productDto);

    @Mapping(target = "product.store", ignore = true)
    protected abstract OrderProductDto serialize(OrderProduct product);
  }
}
