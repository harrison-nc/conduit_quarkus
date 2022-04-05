package dev.nye.conduit.user;

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
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@QuarkusTest
public class UserResourceTest {

  @Inject EntityManager entityManager;

  @Inject UserTransaction userTransaction;

  @Inject
  @ConfigProperty(name = "quarkus.hibernate-orm.database.default-schema")
  String databaseSchema;

  @TestHTTPEndpoint(UserResource.class)
  @TestHTTPResource
  URI uri;

  WebTarget webTarget;
  Client webClient;

  private static Stream<User> users() {
    return Stream.of(new User("test@mail.com", "test", "This is a test", "test.jpeg"));
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

  @BeforeEach
  void setup() {
    webClient = ClientBuilder.newClient();
    webTarget = webClient.target(uri);
  }

  private void removeAllRecordsInDatabase() {
    withTransaction(
        tx -> {
          entityManager.createQuery("DELETE FROM User").executeUpdate();
          entityManager.createNativeQuery("DELETE FROM " + databaseSchema + ".logins").executeUpdate();
        });
  }

  @BeforeEach
  void initDatabase() {
    removeAllRecordsInDatabase();
  }

  @AfterEach
  void cleanupDatabase() {
    removeAllRecordsInDatabase();
  }

  @AfterEach
  void tearDown() {
    webClient.close();
  }

  private JsonObject toJson(User user) {
    return Json.createObjectBuilder()
        .add("email", user.getEmail())
        .add("username", user.getUsername())
        .add("bio", user.getBio())
        .add("image", user.getImage())
        .build();
  }

  @DisplayName("getUser should return 401 if user does not provide an authorization token")
  @MethodSource("users")
  @ParameterizedTest
  void getUser0(User user) {
    Response response = webTarget.request(MediaType.APPLICATION_JSON).get();

    Assertions.assertEquals(401, response.getStatus(), "status");
  }
}
