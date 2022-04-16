package dev.nye.conduit.login.impl;

import dev.nye.conduit.common.JwtGenerator;
import dev.nye.conduit.login.*;
import dev.nye.conduit.login.user.User;
import dev.nye.conduit.login.user.UserEntity;
import dev.nye.conduit.login.user.UserService;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class LoginServiceImpl implements LoginService {

  private final JwtGenerator jwtGenerator;
  private final EntityManager entityManager;
  private final LoginMapper mapper;
  private final UserService userService;

  private Logger logger;

  @Inject
  public LoginServiceImpl(
      EntityManager entityManager,
      LoginMapper mapper,
      JwtGenerator jwtGenerator,
      @RestClient UserService userService) {
    this.entityManager = entityManager;
    this.mapper = mapper;
    this.jwtGenerator = jwtGenerator;
    this.userService = userService;
  }

  @Inject
  public void setLogger(Logger logger) {
    this.logger = logger;
  }

  private Optional<UserEntity> findByEmail(Login login) {
    return entityManager
        .createNamedQuery("findByEmail", UserEntity.class)
        .setParameter("email", login.email())
        .getResultList()
        .stream()
        .findFirst();
  }

  User generateToken(User user) {
    Objects.requireNonNull(user);
    return user.withToken(
        jwtGenerator.generateJwt(Map.of("upn", user.getEmail(), "username", user.getUsername())));
  }

  User requestUser(User user) {
    try {
      String token = "Bearer " + user.getToken();
      return userService.getUser(token).user();
    } catch (WebApplicationException e) {
      throw new WebApplicationException(e, 500);
    }
  }

  @Transactional(Transactional.TxType.SUPPORTS)
  @Override
  public Optional<User> login(@NotNull @Valid LoginRequest request) {
    return findByEmail(request.user())
        .map(mapper::toDomain)
        .map(this::generateToken)
        .map(this::requestUser)
        .map(this::generateToken);
  }
}
