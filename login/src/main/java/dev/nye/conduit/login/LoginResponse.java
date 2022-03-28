package dev.nye.conduit.login;

public record LoginResponse(String email, String username, String bio) implements LoginUser {}
