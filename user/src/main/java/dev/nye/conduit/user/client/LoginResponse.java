package dev.nye.conduit.user.client;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public record LoginResponse(long loginId) {

  @JsonbCreator
  public LoginResponse(@JsonbProperty("loginId") long loginId) {
    this.loginId = loginId;
  }
}
