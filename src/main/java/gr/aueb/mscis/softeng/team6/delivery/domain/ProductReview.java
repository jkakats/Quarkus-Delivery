package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue ("PR")
public class ProductReview extends Review{
}
