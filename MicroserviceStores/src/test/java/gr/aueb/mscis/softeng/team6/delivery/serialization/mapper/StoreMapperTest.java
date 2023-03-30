package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Store;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.StoreDto;
import io.quarkus.test.junit.QuarkusTest;
import java.util.List;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class StoreMapperTest {
  private static final Long TEST_ID = 233L;
  private static final String TEST_NAME = "Krithamos";
  private static final String TEST_TYPE = "restaurant";

  @Inject protected StoreMapper mapper;

  @Test
  void testDeserialize() {
    var dto = new StoreDto(TEST_ID, TEST_NAME, TEST_TYPE, null, List.of());
    assertThat(mapper.deserialize(dto))
        .returns(dto.id(), Store::getId)
        .returns(dto.name(), Store::getName)
        .returns(dto.type(), Store::getType);
  }

  @Test
  void testSerialize() {
    var store = new Store().setId(TEST_ID).setName(TEST_NAME).setType(TEST_TYPE);
    assertThat(mapper.serialize(store))
        .returns(store.getId(), StoreDto::id)
        .returns(store.getName(), StoreDto::name)
        .returns(store.getType(), StoreDto::type)
        .returns(store.getAreas(), StoreDto::areas);
  }
}
