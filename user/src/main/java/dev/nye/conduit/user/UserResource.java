package dev.nye.conduit.user;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/api/users")
public class UserResource {

  @Inject UserService service;

  @POST
  public User register(Registration reg) {
    return new User("", "", "", "", "");
  }
}
