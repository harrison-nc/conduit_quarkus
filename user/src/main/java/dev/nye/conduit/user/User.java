package dev.nye.conduit.user;

import java.util.Objects;
import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public final class User {
  private final String email;
  private final String username;
  private final String bio;
  private final String image;

  @JsonbCreator
  public User(
      @JsonbProperty(value = "email", nillable = true) String email,
      @JsonbProperty(value = "username", nillable = true) String username,
      @JsonbProperty(value = "bio", nillable = true) String bio,
      @JsonbProperty(value = "image", nillable = true) String image) {
    this.email = email;
    this.username = username;
    this.bio = bio;
    this.image = image;
  }

  public String getEmail() {
    return email;
  }

  public String getUsername() {
    return username;
  }

  public String getBio() {
    return bio;
  }

  public String getImage() {
    return image;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (User) obj;
    return Objects.equals(this.email, that.email)
        && Objects.equals(this.username, that.username)
        && Objects.equals(this.bio, that.bio)
        && Objects.equals(this.image, that.image);
  }

  @Override
  public int hashCode() {
    return Objects.hash(email, username, bio, image);
  }

  @Override
  public String toString() {
    return "User["
        + "email="
        + email
        + ", "
        + "username="
        + username
        + ", "
        + "bio="
        + bio
        + ", "
        + "image="
        + image
        + ']';
  }
}
