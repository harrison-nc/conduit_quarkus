package dev.nye.conduit.login.impl;

import dev.nye.conduit.login.*;
import dev.nye.conduit.login.LoginResponse.Failure;
import io.smallrye.common.constraint.NotNull;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.util.Optional;

@ApplicationScoped
public class LoginServiceImpl implements LoginService {

  @Inject EntityManager entityManager;

  @Inject LoginMapper mapper;

  Optional<LoginEntity> findByEmail(LoginRequest req) {
    var results =
        entityManager
            .createNamedQuery("findLoginByEmail", LoginEntity.class)
            .setParameter(1, req.user().email())
            .getResultList();
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public LoginResponse login(@NotNull @Valid LoginRequest user) {
    return findByEmail(user).map(mapper::toDomain).orElse(new Failure());
  }
}
