package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import gr.aueb.mscis.softeng.team6.delivery.domain.Order;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderDto;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.OrderReviewDto;
import io.quarkus.test.junit.QuarkusTest;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class OrderMapperTest {
  private static final UUID TEST_ID = UUID.randomUUID();
  private static final boolean TEST_CONFIRMED = true;
  private static final boolean TEST_DELIVERED = true;
  private static final LocalDateTime TEST_ORDERED_TIME = LocalDateTime.of(2015, 3, 14, 9, 26, 36);
  private static final LocalDateTime TEST_DELIVERED_TIME = LocalDateTime.of(2015, 3, 14, 9, 57, 45);
  private static final Long TEST_ESTIMATED_WAIT = 1869L;
  private static final short TEST_RATING = 3;

  @Inject protected OrderMapper mapper;

  @Test
  void testDeserialize() {
    var dto =
        new OrderDto(
            TEST_ID,
            TEST_CONFIRMED,
            TEST_DELIVERED,
            TEST_ORDERED_TIME,
            TEST_DELIVERED_TIME,
            TEST_ESTIMATED_WAIT,
            new OrderReviewDto(TEST_RATING, ""),
            null,
            null,
            Set.of());
    assertThat(mapper.deserialize(dto))
        .returns(dto.uuid(), Order::getUuid)
        .returns(dto.confirmed(), Order::isConfirmed)
        .returns(dto.delivered(), Order::isDelivered)
        .returns(dto.orderedTime(), Order::getOrderedTime)
        .returns(dto.deliveredTime(), Order::getDeliveredTime)
        .returns(dto.estimatedWait(), Order::getEstimatedWait)
        .returns(dto.review().rating(), o -> o.getReview().getRating());
  }

  @Test
  void testSerialize() {
    var order =
        new Order()
            .setUuid(TEST_ID)
            .setConfirmed(TEST_CONFIRMED)
            .setDelivered(TEST_DELIVERED)
            .setDeliveredTime(TEST_DELIVERED_TIME)
            .setEstimatedWait(TEST_ESTIMATED_WAIT);
    assertThat(mapper.serialize(order))
        .returns(order.getUuid(), OrderDto::uuid)
        .returns(order.isConfirmed(), OrderDto::confirmed)
        .returns(order.isDelivered(), OrderDto::delivered)
        .returns(order.getOrderedTime(), OrderDto::orderedTime)
        .returns(order.getDeliveredTime(), OrderDto::deliveredTime)
        .returns(order.getEstimatedWait(), OrderDto::estimatedWait);
  }
}
