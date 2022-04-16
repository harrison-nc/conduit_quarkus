package dev.nye.conduit.registration;

public interface RegistrationMapper {

  RegistrationEntity toEntity(RegistrationRequest req);

  RegistrationResponse toDomain(RegistrationRequest req);
}
