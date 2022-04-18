package dev.nye.conduit.user.impl;

import dev.nye.conduit.user.User;
import dev.nye.conduit.user.UserEntity;
import dev.nye.conduit.user.UserMapper;
import dev.nye.conduit.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@ApplicationScoped
public class UserServiceImpl implements UserService {

  @Inject EntityManager entityManager;

  @Inject UserMapper mapper;

  private List<UserEntity> findByEmail(String user) {
    return entityManager.createNamedQuery("findByEmail", UserEntity.class)
    .setParameter("email", user)
    .getResultList();
  }

  @Override
  public Optional<User> getUser(String email) {
    Objects.requireNonNull(email);
    return findByEmail(email).stream().map(mapper::toDomain).findFirst();
  }

  private UserEntity updateEntity(UserEntity entity, User user) {
    entity.setBio(user.bio());
    entity.setEmail(user.email());
    entity.setImage(user.image());
    entity.setUsername(user.username());
    return entity;
  }

  @Transactional(Transactional.TxType.REQUIRES_NEW)
  @Override
  public Optional<User> updateUser(User user) {
    return findByEmail(user.email())
      .stream()
      .findFirst()
      .map(entity -> updateEntity(entity, user))
      .map(mapper::toDomain);
  }
}
