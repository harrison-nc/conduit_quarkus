package dev.nye.conduit.login.impl;

import dev.nye.conduit.login.Login;
import dev.nye.conduit.login.LoginService;
import io.smallrye.common.constraint.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
import java.util.Map;

@ApplicationScoped
public class LoginServiceImpl implements LoginService {

  @Override
  public Map<String, Object> login(@NotNull @Valid Login login) {
    return Map.of("email", login.email());
  }
}
