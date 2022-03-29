package dev.nye.conduit.login;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

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

  @DisplayName("Login should return user email")
  @MethodSource("getLogins")
  @ParameterizedTest
  void login0(JsonObject login) {
    var requestBody = Entity.json(login);
    var response = webTarget.request(MediaType.APPLICATION_JSON).post(requestBody);

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "has entity");
    Assertions.assertAll(
        () -> {
          JsonObject responseBody = response.readEntity(JsonObject.class);

          Assertions.assertTrue(responseBody.containsKey("user"), "contains user");
          Assertions.assertAll(
              () -> {
                JsonObject user = responseBody.getJsonObject("user");
                String userEmail = login.getJsonObject("user").getString("email");

                Assertions.assertEquals(userEmail, user.getString("email"), "email");
              });
        });
  }

  @DisplayName("Login should return user properties")
  @MethodSource("getLoginProperties")
  @ParameterizedTest
  void login2(String property, JsonObject login) {
    var requestBody = Entity.json(login);
    var response = webTarget.request(MediaType.APPLICATION_JSON).post(requestBody);

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "has entity");
    Assertions.assertAll(
        () -> {
          var responseBody = response.readEntity(JsonObject.class);
          var user = responseBody.getJsonObject("user");

          Assertions.assertNotNull(user.get(property), property);
        });
  }

  public static List<JsonObject> getLogins() {
    var login =
        Json.createObjectBuilder()
            .add(
                "user",
                Json.createObjectBuilder()
                    .add("email", "john@mail.com")
                    .add("password", "john_password")
                    .build())
            .build();
    return List.of(login);
  }

  public static Stream<Arguments> getLoginProperties() {
    return getLogins().stream()
        .flatMap(
            login ->
                Stream.of("username", "bio", "image", "token")
                    .map(property -> Arguments.arguments(property, login)));
  }
}
