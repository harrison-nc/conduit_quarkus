package dev.nye.conduit.user;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserResourceTest {

  WebTarget webTarget;
  Client webClient;

  @TestHTTPEndpoint(UserResource.class)
  @TestHTTPResource
  URI uri;

  @BeforeEach
  void setup() {
    webClient = ClientBuilder.newClient();
    webTarget = webClient.target(uri);
  }

  @Test
  void register() {
    var reg = new Registration(new Registration.User("alice", "alice@mail.com", "alice_password"));
    var response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(reg));

    Assertions.assertEquals(200, response.getStatus(), "status");
  }
}
