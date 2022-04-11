package dev.nye.conduit.login;

import dev.nye.conduit.login.user.User;

import java.util.Optional;

public interface LoginService {

  Optional<User> login(LoginRequest req);
}
