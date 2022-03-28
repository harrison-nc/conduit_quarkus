package dev.nye.conduit.login;

public sealed interface LoginUser permits LoginRequest, LoginResponse {}
