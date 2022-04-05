package dev.nye.conduit.user.impl;

import dev.nye.conduit.user.User;
import dev.nye.conduit.user.UserEntity;
import dev.nye.conduit.user.UserMapper;
import dev.nye.conduit.user.UserService;
import java.util.Objects;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

@ApplicationScoped
public class UserServiceImpl implements UserService {

  @Inject EntityManager entityManager;

  @Inject UserMapper mapper;

  @Override
  public Optional<User> getUser(String email) {
    Objects.requireNonNull(email);
    var foundUsers = entityManager.createNamedQuery("findByEmail", UserEntity.class).getResultList();
    return foundUsers.stream().map(mapper::toDomain).findFirst();
  }
}
