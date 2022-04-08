package dev.nye.conduit.user.impl;

import dev.nye.conduit.user.User;
import dev.nye.conduit.user.UserEntity;
import dev.nye.conduit.user.UserMapper;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserMapperImpl implements UserMapper {

  @Override
  public UserEntity toEntity(User user) {
    Objects.requireNonNull(user);
    return new UserEntity(user.getEmail(), user.getUsername(), user.getBio(), user.getImage());
  }

  @Override
  public User toDomain(UserEntity entity) {
    Objects.requireNonNull(entity);
    return new User(entity.getEmail(), entity.getUsername(), entity.getBio(), entity.getImage());
  }
}
