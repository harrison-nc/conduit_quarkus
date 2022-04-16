package dev.nye.conduit.registration;

import javax.validation.constraints.NotNull;

public record RegistrationResponse(@NotNull Registration user) {}
