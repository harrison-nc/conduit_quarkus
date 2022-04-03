package dev.nye.conduit.registration.client;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public record LoginRequest(LoginUser user) {

  @JsonbCreator
  public LoginRequest(
      @JsonbProperty("email") String email, @JsonbProperty("password") String password) {
    this(new LoginUser(email, password));
  }
}
