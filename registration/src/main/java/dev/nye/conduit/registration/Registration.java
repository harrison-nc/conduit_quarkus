package dev.nye.conduit.registration;

public record Registration(String username, String email, String bio, String image, String token) {

  public Registration(String username, String email) {
    this(username, email, "", "", "");
  }
}
