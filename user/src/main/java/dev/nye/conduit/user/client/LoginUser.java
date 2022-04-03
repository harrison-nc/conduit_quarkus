package dev.nye.conduit.user.client;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public record LoginUser(String email, String password) {

  @JsonbCreator
  public LoginUser(
      @JsonbProperty("email") String email, @JsonbProperty("password") String password) {
    this.email = email;
    this.password = password;
  }
}
