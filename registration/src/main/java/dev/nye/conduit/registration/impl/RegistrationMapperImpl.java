package dev.nye.conduit.registration.impl;

import dev.nye.conduit.registration.*;
import dev.nye.conduit.registration.client.LoginRequest;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegistrationMapperImpl implements RegistrationMapper {

  @Override
  public RegistrationEntity toEntity(RegistrationRequest req) {
    Objects.requireNonNull(req);
    Objects.requireNonNull(req.user());
    return new RegistrationEntity(req.user().username());
  }

  @Override
  public LoginRequest toLogin(RegistrationRequest req) {
    Objects.requireNonNull(req);
    Objects.requireNonNull(req.user());
    return new LoginRequest(req.user().email(), req.user().password());
  }

  @Override
  public RegistrationResponse toDomain(RegistrationRequest reg) {
    Objects.requireNonNull(reg);
    Objects.requireNonNull(reg.user());
    return new RegistrationResponse(new User(reg.user().username(), reg.user().email()));
  }
}
