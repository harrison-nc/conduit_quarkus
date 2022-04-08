package dev.nye.conduit.user;

import java.util.Map;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/api/user")
public class UserResource {

  @Inject UserService service;
  @Inject JsonWebToken token;

  @GET
  public Map<String, User> getUser() {
    if (token.getName() == null || token.getName().isBlank())
      throw new WebApplicationException(401);

    String email = token.getName();

    return service
        .getUser(email)
        .map(user -> Map.of("user", user))
        .orElseThrow(() -> new WebApplicationException(404));
  }
}
