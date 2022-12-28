package gr.aueb.mscis.softeng.team6.delivery.serialization;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;

/**
 * Converter for {@link LocalDateTime} parameters.
 *
 * @see <a href="https://github.com/quarkusio/quarkus/issues/5860">quarkusio/quarkus#5860</a>
 */
@Provider
public class LocalDateTimeConverter implements ParamConverterProvider {
  @Override
  public <T> ParamConverter<T> getConverter(
      Class<T> rawType, Type genericType, Annotation[] annotations) {
    return rawType != LocalDateTime.class
        ? null
        : new ParamConverter<>() {
          @Override
          public T fromString(String value) {
            //noinspection unchecked
            return (T) LocalDateTime.parse(value);
          }

          @Override
          public String toString(T value) {
            return ((LocalDateTime) value).toString();
          }
        };
  }
}
