package dev.nye.conduit.login;

import dev.nye.conduit.login.LoginResponse.Success;
import dev.nye.conduit.login.LoginResponse.User;
import java.util.Map;

public interface LoginMapper {

  default LoginResponse toDomain(LoginEntity login) {
    return new Success(new User(login.getEmail()));
  }

  default Map<String, Object> toMap(LoginResponse res) {
    return res instanceof Success suc && ((Object) suc.user()) instanceof LoginResponse.User u
        ? Map.of(
            "email", u.email(),
            "username", u.username(),
            "bio", u.bio(),
            "token", u.token())
        : Map.of();
  }
}
