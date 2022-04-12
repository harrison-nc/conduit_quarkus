package dev.nye.conduit.login.user;

import javax.ws.rs.*;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Produces("application/json")
@Consumes("application/json")
@RegisterRestClient
@Path("/api/user")
public interface UserService {

  @GET
  UserResponse getUser(@HeaderParam("Authorization") String token);
}
