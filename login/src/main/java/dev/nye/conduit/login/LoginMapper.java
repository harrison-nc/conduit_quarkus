package dev.nye.conduit.login;

import dev.nye.conduit.login.user.User;
import dev.nye.conduit.login.user.UserEntity;

public interface LoginMapper {

  User toDomain(UserEntity entity);
}
