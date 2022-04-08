package dev.nye.conduit.user;

import dev.nye.conduit.common.JwtGenerator;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.inject.Inject;
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

  @Inject JwtGenerator jwtGenerator;

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

  private Map<String, Object> toMap(User user) {
    return Map.of("upn", user.getEmail());
  }

  private void persistToDatabase(User user) {
    withTransaction(tx -> {
      entityManager.createNativeQuery("""
              INSERT INTO conduit.logins (email, password_hash)
              VALUES (:email, 'test_password')
              """)
              .setParameter("email", user.getEmail())
              .executeUpdate();

      entityManager.createNativeQuery("""
              INSERT INTO conduit.users (email, username, bio, image, login_id)
              VALUES (:email, :username, :bio, :image, (SELECT l.id FROM conduit.logins as l WHERE l.email = :email))
              """)
              .setParameter("email", user.getEmail())
              .setParameter("username", user.getUsername())
              .setParameter("bio", user.getBio())
              .setParameter("image", user.getImage())
              .executeUpdate();
    });
  }

  private Response get(User user) {
    String token = jwtGenerator.generateJwt(toMap(user));
    return webTarget.request(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token)
            .get();
  }

  @DisplayName("getUser should return 401 if user does not provide an authorization token")
  @MethodSource("users")
  @ParameterizedTest
  void getUser0(User ignoredParam) {
    Response response = webTarget.request(MediaType.APPLICATION_JSON).get();

    Assertions.assertEquals(401, response.getStatus(), "status");
  }

  @DisplayName("getUser should return 200 status code if user exists")
  @MethodSource("users")
  @ParameterizedTest
  void getUser1(User user) {
    persistToDatabase(user);
    Response response = get(user);

    Assertions.assertEquals(200, response.getStatus(), "status");
  }

  private void checkJsonResponse(Response response, User user) {
    JsonObject entity = response.readEntity(JsonObject.class);
    JsonObject userJson = entity.getJsonObject("user");

    String email = userJson.getString("email");
    String username = userJson.getString("username");
    String bio = userJson.getString("bio");
    String image = userJson.getString("image");

    Assertions.assertEquals(user.getEmail(), email, "email");
    Assertions.assertEquals(user.getUsername(), username, "username");
    Assertions.assertEquals(user.getBio(), bio, "bio");
    Assertions.assertEquals(user.getImage(), image, "image");
  }

  @DisplayName("getUser should return user Json")
  @MethodSource("users")
  @ParameterizedTest
  void getUser2(User user) {
    persistToDatabase(user);
    Response response = get(user);

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "entity");

    checkJsonResponse(response, user);
  }

  @DisplayName("getUser should return 404 if user does not exists")
  @MethodSource("users")
  @ParameterizedTest
  void getUser3(User user) {
    Response response = get(user);
    
    Assertions.assertEquals(404, response.getStatus(), "status");
  }
}
