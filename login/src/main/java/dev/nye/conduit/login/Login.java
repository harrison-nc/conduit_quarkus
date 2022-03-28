package dev.nye.conduit.login;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public record Login<T extends LoginUser>(T user) {
    public static Login<LoginResponse> NOT_FOUND = new Login<>(null);

    @JsonbCreator
    public Login(@JsonbProperty("user") T user) {
        this.user = user;
    }
}
