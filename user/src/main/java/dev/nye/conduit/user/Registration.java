package dev.nye.conduit.user;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record Registration(@NotNull @JsonbProperty("user") User user) {

  @JsonbCreator
  public Registration {}

  public record User(
      @NotBlank @JsonbProperty("username") String username,
      @NotBlank @JsonbProperty("email") String email,
      @NotBlank @JsonbProperty("password") String password) {

    @JsonbCreator
    public User {}
  }
}
