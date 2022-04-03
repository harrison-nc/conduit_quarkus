package dev.nye.conduit.login.impl;

import dev.nye.conduit.login.*;
import dev.nye.conduit.login.LoginResponse.Failure;
import dev.nye.conduit.login.LoginResponse.Success;
import io.smallrye.jwt.build.Jwt;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;

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
    Map<String, Object> claims =
        mapper.toMap(res).entrySet().stream()
            .filter(e -> !"token".equals(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    return Jwt.claims(claims).sign();
  }

  LoginResponse generateToken(LoginResponse res) {
    return res instanceof Success s && ((Object) s.user()) instanceof LoginResponse.User u
        ? new Success(
            new LoginResponse.User(u.email(), u.username(), u.bio(), u.image(), getToken(res)))
        : res;
  }

  @Transactional(Transactional.TxType.SUPPORTS)
  @Override
  public LoginResponse login(@NotNull @Valid LoginRequest user) {
    return findByEmail(user).map(mapper::toDomain).map(this::generateToken).orElse(new Failure());
  }

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  @Override
  public long create(LoginRequest req) {
    if (findByEmail(req).isPresent()) throw new WebApplicationException(400);

    var entity = mapper.toEntity(req);
    entityManager.persist(entity);

    return entity.getId();
  }
}
