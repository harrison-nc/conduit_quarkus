package dev.nye.conduit.login;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import javax.validation.constraints.NotBlank;

public record Login(
        @NotBlank @JsonbProperty("email") String email,
        @NotBlank @JsonbProperty("password") String password) {

    @JsonbCreator
    public Login {
    }
}
