package dev.nye.conduit.login;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record LoginRequest(@NotNull @JsonbProperty("user") User user) {

  @JsonbCreator
  public LoginRequest {}

  public record User(
      @NotBlank @JsonbProperty("email") String email,
      @NotBlank @JsonbProperty("password") String password) {

    @JsonbCreator
    public User {}
  }
}
