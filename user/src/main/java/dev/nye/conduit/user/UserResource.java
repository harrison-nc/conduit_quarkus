package dev.nye.conduit.user;

import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/api/user")
public class UserResource {

  @Inject UserService service;
  @Inject JsonWebToken token;

  private void checkAuthentication() {
    if (token.getName() == null || token.getName().isBlank())
      throw new WebApplicationException(401);
  }

  private void checkAuthentication(String email) {
    if (!Objects.equals(token.getName(), email))
      throw new WebApplicationException(405);
  }

  private Map<String, User> toResponseBody(User user) {
    return Map.of("user", user);
  }

  @GET
  public Map<String, User> getUser() {
    checkAuthentication();
    String email = token.getName();

    return service
        .getUser(email)
        .map(this::toResponseBody)
        .orElseThrow(() -> new WebApplicationException(404));
  }

  @PUT
  public Map<String, User> updateUser(Map<String, User> body) {
    checkAuthentication();
    User user = body.get("user");

    if (user == null)
      throw new WebApplicationException(404);

    checkAuthentication(user.email());

    return service
      .updateUser(user)
      .map(this::toResponseBody)
      .orElseThrow(() -> new WebApplicationException(400));
  }
}
