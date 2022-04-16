package dev.nye.conduit.login;

import dev.nye.conduit.login.user.UserResponse;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/users/login")
public class LoginResource {

  @Inject LoginService service;

  @POST
  public UserResponse login(LoginRequest body) {
    return service
        .login(body)
        .map(UserResponse::new)
        .orElseThrow(() -> new WebApplicationException(401));
  }
}
