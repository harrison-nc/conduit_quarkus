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
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

  private void removeAllRecordsInDatabase() {
    withTransaction(tx -> entityManager.createQuery("DELETE FROM User").executeUpdate());
  }

  @BeforeEach
  void setup() {
    webClient = ClientBuilder.newClient();
    webTarget = webClient.target(uri);
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

  private UserEntity toEntity(User user) {
    var entity = new UserEntity();
    entity.setUsername(user.username);
    entity.setImage(user.image);
    entity.setBio(user.bio);
    entity.setEmail(user.email);
    return entity;
  }

  private void persistToDatabase(User user) {
    withTransaction(tx -> entityManager.persist(toEntity(user)));
  }

  private String createAuthenticationToken(User user) {
    return "Bearer " + jwtGenerator.generateJwt(Map.of("upn", user.email));
  }

  private Invocation.Builder request(String token) {
    return webTarget
      .request(MediaType.APPLICATION_JSON)
      .header("Authorization", token);
  }

  private Response get(User user) {
    return request(createAuthenticationToken(user)).get();
  }

  private Response put(User user) {
    var bodyParam = Map.of("user", user);
    return request(createAuthenticationToken(user)).put(Entity.json(bodyParam));
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

    Assertions.assertEquals(user.email, email, "email");
    Assertions.assertEquals(user.username, username, "username");
    Assertions.assertEquals(user.bio, bio, "bio");
    Assertions.assertEquals(user.image, image, "image");
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

  @DisplayName("updateUser should return 200 status code when update is successful")
  @MethodSource("users")
  @ParameterizedTest
  void updateUser3(User user) {
    persistToDatabase(user);
    Response response = put(user);

    Assertions.assertEquals(200, response.getStatus(), "status");
  }

  public final static class User {
    public final String email;
    public final String username;
    public final String bio;
    public final String image;

    public User(String email, String username, String bio, String image) {
      this.email = email;
      this.username = username;
      this.bio = bio;
      this.image = image;
    }
  }
}
