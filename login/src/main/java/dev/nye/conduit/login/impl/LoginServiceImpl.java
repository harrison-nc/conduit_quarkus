package dev.nye.conduit.login.impl;

import dev.nye.conduit.login.*;
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

  Optional<LoginEntity> findByEmail(LoginRequest user) {
    var results =
        entityManager
            .createNamedQuery("findLoginByEmail", LoginEntity.class)
            .setParameter(1, user.email())
            .getResultList();
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public Login<LoginResponse> login(@NotNull @Valid LoginRequest user) {
    return findByEmail(user).map(mapper::toDomain).orElse(Login.NOT_FOUND);
  }
}
