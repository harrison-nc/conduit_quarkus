package dev.nye.conduit.login;

import java.util.Objects;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@NamedQuery(name = "findLoginByEmail", query = "SELECT l FROM LoginEntity as l WHERE l.email = ?1")
@Table(name = "logins")
@Entity
public class LoginEntity {

  private Long id;
  private String email;
  private String password;

  public LoginEntity() {
    // JPA
  }

  public LoginEntity(String email, String password) {
    this.email = email;
    this.password = password;
  }

  @SequenceGenerator(
      name = "loginSeq",
      sequenceName = "login_id_seq",
      allocationSize = 1,
      initialValue = 1)
  @GeneratedValue(generator = "loginSeq")
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
    return this.id != null && o instanceof LoginEntity that && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
