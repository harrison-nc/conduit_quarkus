package dev.nye.conduit.login.user;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;

@Produces("application/json")
@Consumes("application/json")
@RegisterRestClient
@Path("/api/user")
public interface UserService {

    @GET
    UserResponse getUser(@HeaderParam("Authorization") String token);
}
