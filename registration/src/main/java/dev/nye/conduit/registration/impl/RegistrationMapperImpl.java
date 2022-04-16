package dev.nye.conduit.registration.impl;

import dev.nye.conduit.registration.*;
import dev.nye.conduit.registration.Registration;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RegistrationMapperImpl implements RegistrationMapper {

  @Override
  public RegistrationEntity toEntity(RegistrationRequest req) {
    Objects.requireNonNull(req);
    Objects.requireNonNull(req.user());
    return new RegistrationEntity(req.user().email(), req.user().username(), req.user().password());
  }

  @Override
  public RegistrationResponse toDomain(RegistrationRequest reg) {
    Objects.requireNonNull(reg);
    Objects.requireNonNull(reg.user());
    return new RegistrationResponse(new Registration(reg.user().username(), reg.user().email()));
  }
}
