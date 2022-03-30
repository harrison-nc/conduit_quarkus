package dev.nye.conduit.user;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/api/users")
public class UserResource {

  @Inject UserService service;

  @POST
  public RegistrationResponse register(Registration reg) {
    return new RegistrationResponse(new User("", reg.user().email(), "", "", ""));
  }
}
