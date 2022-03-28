package dev.nye.conduit.login;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String email, @NotBlank String password) implements LoginUser {

  @JsonbCreator
  public LoginRequest(@JsonbProperty("email") String email, @JsonbProperty("password") String password) {
    this.email = email;
    this.password = password;
  }
}
