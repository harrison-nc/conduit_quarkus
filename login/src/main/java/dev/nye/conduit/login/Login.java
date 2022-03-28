package dev.nye.conduit.login;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

public record Login(@NotBlank String email, @NotBlank String password) {
  @JsonbCreator
  public Login(@JsonbProperty("email") String email, @JsonbProperty("password") String password) {
    this.email = email;
    this.password = password;
  }
}
