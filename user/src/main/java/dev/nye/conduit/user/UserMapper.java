package dev.nye.conduit.user;

import dev.nye.conduit.user.client.LoginRequest;

public interface UserMapper {

  UserEntity toEntity(Registration req);

  LoginRequest toLogin(Registration req);

  RegistrationResponse toDomain(Registration req);
}
