package dev.nye.conduit.login.user;

import java.io.Serializable;
import java.util.Objects;

public final class User implements Serializable {
  private String email;
  private String username;
  private String bio;
  private String image;
  private String token;

  public User() {
    this("", "", "", "");
  }

  public User(String email) {
    this(email, "", "", "");
  }

  public User(String email, String username, String bio, String image) {
    this(email, username, bio, image, "");
  }

  public User(String email, String username, String bio, String image, String token) {
    this.email = email;
    this.username = username;
    this.bio = bio;
    this.image = image;
    this.token = token;
  }

  public User withToken(String token) {
    return new User(email, username, bio, image, token);
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(email, user.email)
        && Objects.equals(username, user.username)
        && Objects.equals(bio, user.bio)
        && Objects.equals(image, user.image)
        && Objects.equals(token, user.token);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, username, bio, image, token);
  }

  @Override
  public String toString() {
    return "User{"
        + "email='"
        + email
        + '\''
        + ", username='"
        + username
        + '\''
        + ", bio='"
        + bio
        + '\''
        + ", image='"
        + image
        + '\''
        + ", token='"
        + token
        + '\''
        + '}';
  }
}
