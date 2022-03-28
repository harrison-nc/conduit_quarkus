package dev.nye.conduit.login;

public interface LoginMapper {

    default Login toDomain(LoginEntity login) {
        return new Login(login.getEmail(), login.getPassword());
    }

    default LoginEntity toEntity(Login login) {
        return new LoginEntity(login.email(), login.password());
    }
}
