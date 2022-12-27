package gr.aueb.mscis.softeng.team6.delivery.resource;

import java.io.Serializable;

/** Error message wrapper. */
record ErrorMessage(String error) implements Serializable {
  ErrorMessage(Throwable throwable) {
    this(throwable.getMessage());
  }
}
