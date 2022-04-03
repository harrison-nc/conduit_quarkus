package dev.nye.conduit.user.impl;

import dev.nye.conduit.user.*;
import dev.nye.conduit.user.client.LoginResponse;
import dev.nye.conduit.user.client.LoginService;
import java.util.Objects;
import java.util.function.Function;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class UserServiceImpl implements UserService {

  @Inject UserMapper mapper;

  @Inject EntityManager entityManager;

  @RestClient @Inject LoginService loginService;

  private RegistrationResponse toDomain(
      UserEntity entity, Function<UserEntity, RegistrationResponse> fn) {
    Objects.requireNonNull(entity);
    Objects.requireNonNull(fn);
    return fn.apply(entity);
  }

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  @Override
  public RegistrationResponse register(Registration reg) {
    LoginResponse loginId = loginService.createLogin(mapper.toLogin(reg));
    var entity = mapper.toEntity(reg);
    entity.setLoginId(loginId.loginId());
    entityManager.persist(entity);
    return mapper.toDomain(reg);
  }
}
