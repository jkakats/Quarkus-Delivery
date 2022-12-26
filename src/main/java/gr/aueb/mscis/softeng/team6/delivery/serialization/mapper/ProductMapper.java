package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * A mapper for the {@link Product} entity.
 *
 * @since 1.0.0
 */
@Mapper(uses = {StoreMapper.class})
public interface ProductMapper {
  ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

  Product deserialize(ProductDto productDto);

  ProductDto serialize(Product product);
}
