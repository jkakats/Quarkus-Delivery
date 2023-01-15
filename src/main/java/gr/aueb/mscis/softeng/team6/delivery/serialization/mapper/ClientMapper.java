package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * A mapper for the {@link Client} entity.
 *
 * @since 1.0.0
 */
@Mapper(componentModel = "cdi", injectionStrategy = CONSTRUCTOR)
public interface ClientMapper {
  @SuppressWarnings("UnmappedTargetProperties")
  @Mapping(target = "password", ignore = true)
  @Mapping(source = "email", target = "email.email")
  @Mapping(source = "phoneNumber", target = "phone.number")
  Client deserialize(ClientDto clientDto);

  @InheritInverseConfiguration(name = "deserialize")
  ClientDto serialize(Client client);
}
