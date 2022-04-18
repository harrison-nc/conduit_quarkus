package dev.nye.conduit.user;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public record User(String email, String username, String bio, String image) {

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
}
