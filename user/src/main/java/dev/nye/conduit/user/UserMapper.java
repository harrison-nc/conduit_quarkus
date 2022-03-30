package dev.nye.conduit.user;

import dev.nye.conduit.login.LoginEntity;
import java.util.Objects;

public interface UserMapper {

  default UserEntity toEntity(Registration req) {
    Objects.requireNonNull(req);
    Objects.requireNonNull(req.user());
    return new UserEntity(
        req.user().username(), new LoginEntity(req.user().email(), req.user().password()));
  }
}
