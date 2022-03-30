package dev.nye.conduit.user;

import dev.nye.conduit.login.LoginEntity;
import java.util.Objects;
import javax.persistence.*;

@Table(name = "users")
@Entity(name = "User")
public class UserEntity {

  private Long id;
  private String username;
  private LoginEntity login;

  public UserEntity() {
    // JPA
  }

  public UserEntity(String username, LoginEntity login) {
    this.username = username;
    this.login = login;
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

  @OneToOne(
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      optional = false,
      orphanRemoval = true)
  public LoginEntity getLogin() {
    return login;
  }

  public void setLogin(LoginEntity login) {
    this.login = login;
  }

  @Column(name = "username")
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    return id != null && o instanceof UserEntity other && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
