package dev.nye.conduit.registration;

import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Table(name = "users")
@Entity(name = "User")
public class RegistrationEntity {

  private Long id;
  private String email;
  private String username;
  private String password;
  private String bio;
  private String image;

  public RegistrationEntity() {
    // JPA
  }

  public RegistrationEntity(String email, String username, String password) {
    this(email, username, password, "", "");
  }

  public RegistrationEntity(
      String email, String username, String password, String bio, String image) {
    this.email = email;
    this.username = username;
    this.password = password;
    this.bio = bio;
    this.image = image;
  }

  @SequenceGenerator(
      name = "userSeq",
      sequenceName = "user_id_seq",
      allocationSize = 1,
      initialValue = 100)
  @GeneratedValue(generator = "userSeq")
  @Column(name = "id")
  @Id
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "username", nullable = false)
  @NotBlank
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Column(name = "email", nullable = false)
  @NotBlank
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "password_hash", nullable = false)
  @NotBlank
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Column(name = "bio")
  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  @Column(name = "image")
  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  @Override
  public boolean equals(Object o) {
    return id != null && o instanceof RegistrationEntity other && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
