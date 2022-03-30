package dev.nye.conduit.user;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.stream.Stream;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

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

  @AfterEach
  void tearDown() {
    webClient.close();
  }

  @DisplayName("register should return 200 status code")
  @MethodSource("registrations")
  @ParameterizedTest
  void register(JsonObject reg) {
    var response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(reg));

    Assertions.assertEquals(200, response.getStatus(), "status");
  }

  @DisplayName("register should return a json object with a user property")
  @MethodSource("registrations")
  @ParameterizedTest
  void register1(JsonObject reg, TestReporter reporter) {
    var response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(reg));

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "entity");
    Assertions.assertAll(
        () -> {
          var entity = response.readEntity(JsonObject.class);

          Assertions.assertNotNull(entity.getJsonObject("user"), "user");
        });
  }

  private static JsonObject toJsonObject(Registration reg) {
    return Json.createObjectBuilder()
        .add(
            "user",
            Json.createObjectBuilder()
                .add("email", reg.user().email())
                .add("username", reg.user().username())
                .add("password", reg.user().password())
                .build())
        .build();
  }

  public static Stream<JsonObject> registrations() {
    var alice =
        new Registration(new Registration.User("alice", "alice@mail.com", "alice_password"));
    return Stream.of(toJsonObject(alice));
  }
}
