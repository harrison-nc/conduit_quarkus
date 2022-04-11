package dev.nye.conduit.login.impl;

import dev.nye.conduit.login.*;
import dev.nye.conduit.login.user.UserService;
import dev.nye.conduit.login.user.User;
import dev.nye.conduit.login.user.UserEntity;
import io.smallrye.jwt.build.Jwt;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Objects;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.WebApplicationException;

@ApplicationScoped
public class LoginServiceImpl implements LoginService {

  EntityManager entityManager;
  LoginMapper mapper;
  UserService userService;

  @Inject
  public LoginServiceImpl(EntityManager entityManager, LoginMapper mapper, @RestClient UserService userService) {
    this.entityManager = entityManager;
    this.mapper = mapper;
    this.userService = userService;
  }

  private Optional<UserEntity> findByEmail(Login login) {
    return entityManager.createNamedQuery("findByEmail", UserEntity.class)
            .setParameter("email", login.email())
            .getResultList()
            .stream()
            .findFirst();
  }

  String getToken(User user) {
    return Jwt.claims()
            .upn(user.getEmail())
            .claim("username", user.getUsername())
            .sign();
  }

  User generateToken(User user) {
    Objects.requireNonNull(user);
    return user.withToken(getToken(user));
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
