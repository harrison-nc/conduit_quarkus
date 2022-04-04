package dev.nye.conduit.registration;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
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
import javax.ws.rs.core.Response;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
public class RegistrationResourceTest {

  WebTarget webTarget;
  Client webClient;

  @TestHTTPEndpoint(RegistrationResource.class)
  @TestHTTPResource
  URI uri;

  @Inject EntityManager entityManager;

  @Inject UserTransaction userTransaction;

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
    }
  }

  @BeforeEach
  void setup() {
    webClient = ClientBuilder.newClient();
    webTarget = webClient.target(uri);
  }

  @AfterEach
  void tearDown() {
    webClient.close();
  }

  @BeforeEach
  @AfterEach
  void clearDb() {
    withTransaction(
        tx -> {
          entityManager.createQuery("DELETE FROM User").executeUpdate();
          entityManager.createNativeQuery("DELETE FROM logins").executeUpdate();
        });
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

  @DisplayName("register should return a matching value for required properties")
  @MethodSource("registrationsWithRequiredProperties")
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
                Assertions.assertEquals(
                    reg.getJsonObject("user").get(property), user.get(property), property);
              });
        });
  }

  @DisplayName("register may return optional properties")
  @MethodSource("registrationsWithOptionalProperties")
  @ParameterizedTest
  void register2_5(String property, JsonObject reg) {
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

  @DisplayName("register should return an empty user token property")
  @MethodSource("registrations")
  @ParameterizedTest
  void register3(JsonObject reg) {
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
                String token = user.getString("token");

                Assertions.assertTrue(token == null || token.isBlank());
              });
        });
  }

  @DisplayName("register should persist user to database")
  @MethodSource("registrations")
  @ParameterizedTest
  void register5(JsonObject reg) {
    String email = reg.getJsonObject("user").getString("email");

    var response = post(reg);

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertDoesNotThrow(
        () ->
            withTransaction(
                tx -> {
                  var loginId =
                      entityManager
                          .createNativeQuery("SELECT l.id FROM logins l WHERE l.email = :email")
                          .setParameter("email", email)
                          .getSingleResult();

                  entityManager
                      .createQuery(
                          "SELECT u FROM User u WHERE u.loginId = :loginId",
                          RegistrationEntity.class)
                      .setParameter("loginId", ((Number) loginId).longValue())
                      .getSingleResult();
                }),
        "user");
  }

  private static JsonObject toJsonObject(RegistrationRequest reg) {
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
        new RegistrationRequest(
            new RegistrationRequest.User("test", "test@mail.com", "test_password"));
    return Stream.of(toJsonObject(alice));
  }

  public static Stream<Arguments> registrationsWithRequiredProperties() {
    return registrations()
        .flatMap(
            reg ->
                Stream.of("email", "username").map(property -> Arguments.arguments(property, reg)));
  }

  public static Stream<Arguments> registrationsWithOptionalProperties() {
    return registrations()
        .flatMap(
            reg ->
                Stream.of("bio", "image", "token")
                    .map(property -> Arguments.arguments(property, reg)));
  }
}
