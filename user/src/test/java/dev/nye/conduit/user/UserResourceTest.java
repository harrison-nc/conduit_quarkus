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
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

  private Response post(JsonObject reg) {
    return webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(reg));
  }

  @DisplayName("register should return 200 status code")
  @MethodSource("registrations")
  @ParameterizedTest
  void register(JsonObject reg) {
    var response = post(reg);

    Assertions.assertEquals(200, response.getStatus(), "status");
  }

  @DisplayName("register should return a json object with a user property")
  @MethodSource("registrations")
  @ParameterizedTest
  void register1(JsonObject reg) {
    var response = post(reg);

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "entity");
    Assertions.assertAll(
        () -> {
          var entity = response.readEntity(JsonObject.class);

          Assertions.assertNotNull(entity.getJsonObject("user"), "user");
        });
  }

  @DisplayName("register should return the following properties")
  @MethodSource("registrationsWithProperty")
  @ParameterizedTest
  void register2(String property, JsonObject reg) {
    var response = post(reg);

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "entity");
    Assertions.assertAll(
        () -> {
          var entity = response.readEntity(JsonObject.class);

          Assertions.assertNotNull(entity, "entity");
          Assertions.assertNotNull(entity.getJsonObject("user"), "user");
          Assertions.assertAll(
              () -> {
                var user = entity.getJsonObject("user");

                Assertions.assertNotNull(user.get(property), property);
                Assertions.assertAll(
                    () -> {
                      if (reg.containsKey(property))
                        Assertions.assertEquals(
                            reg.getJsonObject("user").get(property), user.get(property), property);
                    });
              });
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

  public static Stream<Arguments> registrationsWithProperty() {
    return registrations()
        .flatMap(
            reg ->
                Stream.of("email", "username", "bio")
                    .map(property -> Arguments.arguments(property, reg)));
  }
}
