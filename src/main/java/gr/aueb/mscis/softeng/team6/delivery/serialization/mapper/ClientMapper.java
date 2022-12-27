package gr.aueb.mscis.softeng.team6.delivery.serialization.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import gr.aueb.mscis.softeng.team6.delivery.domain.Client;
import gr.aueb.mscis.softeng.team6.delivery.serialization.dto.ClientDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * A mapper for the {@link Client} entity.
 *
 * @since 1.0.0
 */
@Mapper(componentModel = "cdi", injectionStrategy = CONSTRUCTOR)
public abstract class ClientMapper {
  @SuppressWarnings("UnmappedTargetProperties")
  @Mapping(target = "orders", ignore = true)
  @Mapping(source = "email", target = "email.email")
  @Mapping(source = "phoneNumber", target = "phone.number")
  @Mapping(source = "password", target = "password.password")
  public abstract Client deserialize(ClientDto clientDto);

  @InheritInverseConfiguration(name = "deserialize")
  public abstract ClientDto serialize(Client client);

  @Mapping(target = "uuid", ignore = true)
  @Mapping(target = "username", ignore = true)
  @Mapping(target = "orders", ignore = true)
  @Mapping(target = "email", expression = "java(new EmailAddress(clientDto.email()))")
  @Mapping(target = "phone", expression = "java(new PhoneNumber(clientDto.phoneNumber()))")
  @Mapping(target = "password", expression = "java(new Password(clientDto.password()))")
  public abstract void update(@MappingTarget Client client, ClientDto clientDto);
}
