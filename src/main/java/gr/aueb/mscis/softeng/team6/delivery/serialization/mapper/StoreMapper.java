package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orders", ignore = true)
  public abstract void update(@MappingTarget Store store, StoreDto storeDto);
}
