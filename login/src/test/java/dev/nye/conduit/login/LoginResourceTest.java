package dev.nye.conduit.login;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.transaction.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
public class LoginResourceTest {

  @TestHTTPEndpoint(LoginResource.class)
  @TestHTTPResource
  URI uri;

  @Inject EntityManager entityManager;

  @Inject UserTransaction userTransaction;

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

  private void withTransaction(Consumer<UserTransaction> block) {
    try {
      userTransaction.begin();
      block.accept(userTransaction);
      userTransaction.commit();
    } catch (HeuristicRollbackException
        | SystemException
        | HeuristicMixedException
        | NotSupportedException
        | RollbackException e) {
      try {
        userTransaction.rollback();
      } catch (SystemException ex) {
        throw new AssertionError(ex);
      }
      throw new AssertionError(e);
    }
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

  @DisplayName("Login should return 401 if user doest not exists")
  @Test
  void login_reject() {

    JsonObject login =
        Json.createObjectBuilder()
            .add(
                "user",
                Json.createObjectBuilder()
                    .add("email", "bob@mail.com")
                    .add("password", "bob_password")
                    .build())
            .build();

    withTransaction(
        tx -> {
          entityManager
              .createNamedQuery("deleteLoginByEmail")
              .setParameter(1, login.getJsonObject("user").getString("email"))
              .executeUpdate();

          List<LoginEntity> entities =
              entityManager
                  .createNamedQuery("findLoginByEmail", LoginEntity.class)
                  .setParameter(1, login.getJsonObject("user").getString("email"))
                  .getResultList();

          Assertions.assertEquals(0, entities.size(), "entities");
        });

    var response = webTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(login));

    Assertions.assertEquals(401, response.getStatus(), "status");
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
