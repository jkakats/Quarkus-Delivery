package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import org.mapstruct.Mapper;

/**
 * A mapper for the {@link Store} entity.
 *
 * @since 1.0.0
 */
@Mapper(
    componentModel = "cdi",
    injectionStrategy = CONSTRUCTOR,
    uses = {ProductMapper.class})
public interface StoreMapper {
  Store deserialize(StoreDto storeDto);

  StoreDto serialize(Store store);
}
