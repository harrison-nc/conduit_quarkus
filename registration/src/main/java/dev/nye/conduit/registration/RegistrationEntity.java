package dev.nye.conduit.registration;

import java.util.Objects;
import javax.persistence.*;

@Table(name = "users")
@Entity(name = "User")
public class RegistrationEntity {

  private Long id;
  private String username;
  private Long loginId;

  public RegistrationEntity() {
    // JPA
  }

  public RegistrationEntity(String username) {
    this(username, 0L);
  }

  public RegistrationEntity(String username, Long loginId) {
    this.username = username;
    this.loginId = loginId;
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

  @Column(name = "login_id")
  public Long getLoginId() {
    return loginId;
  }

  public void setLoginId(Long loginId) {
    this.loginId = loginId;
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
    return id != null && o instanceof RegistrationEntity other && id.equals(other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
