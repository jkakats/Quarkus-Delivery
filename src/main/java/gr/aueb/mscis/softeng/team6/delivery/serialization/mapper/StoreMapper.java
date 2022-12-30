package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

/**
 * A mapper for the {@link Store} entity.
 *
 * @since 1.0.0
 */
@Mapper(
    componentModel = "cdi",
    injectionStrategy = CONSTRUCTOR,
    uses = {ProductMapper.class})
public abstract class StoreMapper {
  @Mapping(target = "orders", ignore = true)
  public abstract Store deserialize(StoreDto storeDto);

  public abstract StoreDto serialize(Store store);

  @IterableMapping(qualifiedByName = "serializeSimple")
  public abstract List<StoreDto> serialize(List<Store> stores);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orders", ignore = true)
  public abstract void update(@MappingTarget Store store, StoreDto storeDto);

  @Named("serializeSimple")
  @Mapping(target = "products", qualifiedByName = "serializeSimple")
  abstract StoreDto serializeSimple(Store store);
}
