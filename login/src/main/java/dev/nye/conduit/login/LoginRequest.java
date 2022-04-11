package dev.nye.conduit.login;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotNull;

public record LoginRequest(@NotNull @JsonbProperty("user") Login user) {

  @JsonbCreator
  public LoginRequest {}
}
