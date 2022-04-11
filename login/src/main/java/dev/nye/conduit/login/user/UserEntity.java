package dev.nye.conduit.login.user;

import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NamedQuery(name = "findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")
@Table(name = "users")
@Entity(name = "User")
public class UserEntity {

  private Long id;
  private String email;
  private String password;

  public UserEntity() {
    // JPA
  }

  public UserEntity(String email, String password) {
    this.email = email;
    this.password = password;
  }

  @Column(name = "id")
  @Id
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Column(name = "email")
  @NotBlank
  @Email
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Column(name = "password_hash")
  @NotBlank
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public boolean equals(Object o) {
    return this.id != null && o instanceof UserEntity that && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
