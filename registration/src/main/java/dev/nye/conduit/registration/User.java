package dev.nye.conduit.registration;

public record User(String username, String email, String bio, String image, String token) {

  public User(String username, String email) {
    this(username, email, "", "", "");
  }
}
