package dev.nye.conduit.user;

public record User(String username, String email, String bio, String image, String token) {

  public User(String username, String email) {
    this(username, email, "", "", "");
  }
}
