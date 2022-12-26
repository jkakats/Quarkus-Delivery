package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * A mapper for the {@link Store} entity.
 *
 * @since 1.0.0
 */
@Mapper(uses = {ProductMapper.class})
public interface StoreMapper {
  StoreMapper INSTANCE = Mappers.getMapper(StoreMapper.class);

  Store deserialize(StoreDto storeDto);

  StoreDto serialize(Store store);
}
