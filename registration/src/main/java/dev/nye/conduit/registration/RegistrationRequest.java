package dev.nye.conduit.registration;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public record RegistrationRequest(@NotNull @JsonbProperty("user") User user) {

  @JsonbCreator
  public RegistrationRequest {}

  public record User(
      @NotBlank @JsonbProperty("username") String username,
      @NotBlank @JsonbProperty("email") String email,
      @NotBlank @JsonbProperty("password") String password) {

    @JsonbCreator
    public User {}
  }
}
