package dev.nye.conduit.login;

import dev.nye.conduit.login.LoginResponse.User;

public interface LoginMapper {

  default LoginResponse toDomain(LoginEntity login) {
    return new LoginResponse.Success(new User(login.getEmail()));
  }

  default LoginEntity toEntity(LoginRequest req) {
    return new LoginEntity(req.user().email(), req.user().password());
  }
}
