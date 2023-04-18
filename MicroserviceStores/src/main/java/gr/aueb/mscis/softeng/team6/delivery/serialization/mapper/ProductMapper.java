package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Product;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * A mapper for the {@link Product} entity.
 *
 * @since 1.0.0
 */
@Mapper(
    componentModel = "cdi",
    injectionStrategy = CONSTRUCTOR,
    uses = {StoreMapper.class})
public abstract class ProductMapper {

  @Mapping(target = "store.products", ignore = true)
  public abstract Product deserialize(ProductDto productDto);

  @Mapping(target = "store.products", ignore = true)
  public abstract ProductDto serialize(Product product);

  @Mapping(target = "id", ignore = true)

  @Mapping(target = "store.products", ignore = true)
  public abstract void update(@MappingTarget Product product, ProductDto productDto);

  @Named("serializeSimple")
  @Mapping(target = "store", ignore = true)
  abstract ProductDto serializeSimple(Product product);
}
