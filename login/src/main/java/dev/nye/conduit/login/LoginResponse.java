package dev.nye.conduit.login;

public sealed interface LoginResponse {

  record User(String email, String username, String bio, String image, String token) {

    public User(String email) {
      this(email, "");
    }

    public User(String email, String token) {
      this(email, "", "", "", token);
    }
  }

  record Success(User user) implements LoginResponse {}

  record Failure(String msg) implements LoginResponse {

    public Failure() {
      this("");
    }
  }
}
