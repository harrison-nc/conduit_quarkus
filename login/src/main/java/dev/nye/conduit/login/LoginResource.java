package dev.nye.conduit.login;

import dev.nye.conduit.login.LoginResponse.Failure;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/users/login")
public class LoginResource {

  @Inject LoginService service;

  @POST
  public LoginResponse login(LoginRequest body) {
    var login = service.login(body);

    if (login instanceof Failure) throw new WebApplicationException(401);

    return login;
  }
}
