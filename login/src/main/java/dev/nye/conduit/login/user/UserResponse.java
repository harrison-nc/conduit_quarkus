package dev.nye.conduit.login.user;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public record UserResponse(User user) {

    @JsonbCreator
    public UserResponse(@JsonbProperty("user") User user) {
        this.user = user;
    }
}
