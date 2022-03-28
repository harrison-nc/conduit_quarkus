package dev.nye.conduit.login;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/users/login")
public class LoginResource {

  @Inject LoginService service;

  @POST
  public Map<String, Object> login(Map<String, Login> body) {
    Login login = body.get("user");
    return service.login(login);
  }
}
