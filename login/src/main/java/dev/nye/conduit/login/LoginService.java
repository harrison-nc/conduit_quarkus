package dev.nye.conduit.login;

public interface LoginService {

  Login<LoginResponse> login(LoginRequest user);
}
