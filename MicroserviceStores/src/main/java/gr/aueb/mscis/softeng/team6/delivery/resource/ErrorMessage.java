package gr.aueb.mscis.softeng.team6.delivery.resource;

import java.io.Serializable;

/**
 * Error message wrapper.
 *
 * @since 1.0.0
 */
record ErrorMessage(String error) implements Serializable {
  ErrorMessage(Throwable throwable) {
    this(throwable.getMessage());
  }
}
