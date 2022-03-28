package dev.nye.conduit.login;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.URI;

@QuarkusTest
public class LoginResourceTest {

  @TestHTTPEndpoint(LoginResource.class)
  @TestHTTPResource
  URI uri;

  WebTarget webTarget;
  Client webClient;

  @BeforeEach
  void setup() {
    webClient = ClientBuilder.newClient();
    webTarget = webClient.target(uri);
  }

  @AfterEach
  void tearDown() {
    webClient.close();
  }

  @Test
  void login0() {
    var loginEmail = "john@mail.com";
    var login =
        Json.createObjectBuilder()
            .add(
                "user",
                Json.createObjectBuilder()
                    .add("email", loginEmail)
                    .add("password", "john_password")
                    .build())
            .build();

    var requestEntity = Entity.json(login);

    var response = webTarget.request(MediaType.APPLICATION_JSON).post(requestEntity);

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "has entity");

    var responseEntity = response.readEntity(JsonObject.class);

    Assertions.assertEquals(loginEmail, responseEntity.getString("email"), "email");
  }
}
