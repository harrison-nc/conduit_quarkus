package dev.nye.conduit.login.impl;

import dev.nye.conduit.login.*;
import dev.nye.conduit.login.LoginResponse.Failure;
import io.smallrye.common.constraint.NotNull;
import io.smallrye.jwt.build.Jwt;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.Valid;

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

  String getToken(LoginResponse res) {
    Map<String, Object> properties = mapper.toMap(res);
    Map<String, Object> claims =
        properties.entrySet().stream()
            .filter(e -> !"token".equals(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    String token = Jwt.claims(claims).sign();
    System.out.println(token);
    return token;
  }

  LoginResponse generateToken(LoginResponse res) {
    return res instanceof LoginResponse.Success s
            && ((Object) s.user()) instanceof LoginResponse.User u
        ? new LoginResponse.Success(
            new LoginResponse.User(u.email(), u.username(), u.bio(), u.image(), getToken(res)))
        : res;
  }

  @Override
  public LoginResponse login(@NotNull @Valid LoginRequest user) {
    return findByEmail(user).map(mapper::toDomain).map(this::generateToken).orElse(new Failure());
  }
}
