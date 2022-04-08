package dev.nye.conduit.user;

public interface UserMapper {

  UserEntity toEntity(User user);

  User toDomain(UserEntity entity);
}
