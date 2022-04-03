package dev.nye.conduit.user.impl;

import dev.nye.conduit.user.*;
import dev.nye.conduit.user.client.LoginRequest;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapperImpl implements UserMapper {

  @Override
  public UserEntity toEntity(Registration req) {
    Objects.requireNonNull(req);
    Objects.requireNonNull(req.user());
    return new UserEntity(req.user().username());
  }

  @Override
  public LoginRequest toLogin(Registration req) {
    Objects.requireNonNull(req);
    Objects.requireNonNull(req.user());
    return new LoginRequest(req.user().email(), req.user().password());
  }

  @Override
  public RegistrationResponse toDomain(Registration reg) {
    Objects.requireNonNull(reg);
    Objects.requireNonNull(reg.user());
    return new RegistrationResponse(new User(reg.user().username(), reg.user().email()));
  }
}
