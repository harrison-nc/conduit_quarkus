package dev.nye.conduit.login;

import dev.nye.conduit.common.JwtGenerator;
import dev.nye.conduit.login.impl.LoginServiceImpl;
import dev.nye.conduit.login.user.User;
import dev.nye.conduit.login.user.UserService;
import io.quarkus.test.Mock;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
@Mock
public class LoginServiceImpl_ implements LoginService {

  @Inject EntityManager entityManager;

  @Inject LoginMapper mapper;

  @Inject JwtGenerator jwtGenerator;

  @Inject @RestClient UserService userService;

  private LoginService loginService;

  @PostConstruct
  public void init() {
    loginService = new LoginServiceImpl(entityManager, mapper, jwtGenerator, userService);
  }

  @Override
  public Optional<User> login(LoginRequest req) {
    return loginService.login(req);
  }
}
