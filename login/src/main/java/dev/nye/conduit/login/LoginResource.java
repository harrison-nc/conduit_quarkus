package dev.nye.conduit.login;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/users/login")
public class LoginResource {

  @Inject LoginService service;

  @POST
  public Login<LoginResponse> login(Login<LoginRequest> body) {
    var login = service.login(body.user());

    if (login == Login.NOT_FOUND) throw new WebApplicationException(401);

    return login;
  }
}
