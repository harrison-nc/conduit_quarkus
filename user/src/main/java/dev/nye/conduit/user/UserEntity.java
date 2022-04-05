package dev.nye.conduit.user;

import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Email;

@NamedQuery(
    name = "findByEmail",
    query =
        """
    SELECT
        u.email,
        u.username,
        u.bio,
        u.image
    FROM User u
    WHERE u.email = :email
    """)
@Table(name = "users")
@Entity(name = "User")
public class UserEntity {

  private Long id;
  private String email;
  private String username;
  private String bio;
  private String image;
  private Long loginId;

  public UserEntity() {
    // JPA
  }

  public UserEntity(String email, String username, String bio, String image) {
    this.email = email;
    this.username = username;
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

  @Column(name = "email", unique = true)
  @Email
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "username", unique = true)
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Column(name = "login_id")
  public Long getLoginId() {
    return loginId;
  }

  public void setLoginId(Long loginId) {
    this.loginId = loginId;
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
    return id != null && o instanceof UserEntity other && Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
