package gr.aueb.mscis.softeng.team6.delivery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OR")

class OrderReview extends Review {

  @Column(name = "comment")
  private String comment;

  public String getComment () { return comment; }
}
