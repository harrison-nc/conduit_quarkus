package dev.nye.conduit.login;

public record LoginResponse(String email, String username) implements LoginUser {}
