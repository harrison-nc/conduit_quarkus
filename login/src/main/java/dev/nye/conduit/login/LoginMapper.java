package dev.nye.conduit.login;

public interface LoginMapper {

  default Login<LoginResponse> toDomain(LoginEntity login) {
    return new Login<>(new LoginResponse(login.getEmail(), "", ""));
  }

  default LoginEntity toEntity(LoginRequest user) {
    return new LoginEntity(user.email(), user.password());
  }
}
