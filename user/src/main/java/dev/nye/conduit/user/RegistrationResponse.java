package dev.nye.conduit.user;

import javax.validation.constraints.NotNull;

public record RegistrationResponse(@NotNull User user) {}
