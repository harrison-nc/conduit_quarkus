package dev.nye.conduit.registration;

import dev.nye.conduit.registration.client.LoginRequest;

public interface RegistrationMapper {

  RegistrationEntity toEntity(RegistrationRequest req);

  LoginRequest toLogin(RegistrationRequest req);

  RegistrationResponse toDomain(RegistrationRequest req);
}
