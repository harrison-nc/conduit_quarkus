package dev.nye.conduit.login;

import dev.nye.conduit.login.user.User;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/users/login")
public class LoginResource {

  @Inject LoginService service;

  @POST
  public User login(LoginRequest body) {
    return service.login(body).orElseThrow(() -> new WebApplicationException(401));
  }
}
