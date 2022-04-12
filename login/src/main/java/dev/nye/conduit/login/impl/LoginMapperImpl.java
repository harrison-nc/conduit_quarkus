package dev.nye.conduit.login.impl;

import dev.nye.conduit.login.LoginMapper;
import dev.nye.conduit.login.user.User;
import dev.nye.conduit.login.user.UserEntity;
import java.util.Objects;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoginMapperImpl implements LoginMapper {

  @Override
  public User toDomain(UserEntity entity) {
    Objects.requireNonNull(entity);
    return new User(entity.getEmail());
  }
}
