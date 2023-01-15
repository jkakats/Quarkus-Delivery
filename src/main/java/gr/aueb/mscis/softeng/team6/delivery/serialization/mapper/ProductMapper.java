package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import org.mapstruct.Mapper;

/**
 * A mapper for the {@link Product} entity.
 *
 * @since 1.0.0
 */
@Mapper(
    componentModel = "cdi",
    injectionStrategy = CONSTRUCTOR,
    uses = {StoreMapper.class})
public interface ProductMapper {
  Product deserialize(ProductDto productDto);

  ProductDto serialize(Product product);
}
