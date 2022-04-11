package dev.nye.conduit.login;

import dev.nye.conduit.login.user.User;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import javax.transaction.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

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

  public static List<UserWithLogin> getLogins() {
    return List.of(new UserWithLogin(
            new User(
                    "test@mail.com",
                    "test",
                    "A test user",
                    "test.jpeg"),
            new Login(
                    "test@mail.com",
                    "test_password")));
  }

  public static Stream<Arguments> getLoginProperties() {
    return getLogins().stream()
            .flatMap(
                    login ->
                            Stream.of("username", "bio", "image", "token")
                                    .map(property -> Arguments.arguments(property, login)));
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

  private void createUser(UserWithLogin ul) {
    withTransaction(tx -> entityManager.createNativeQuery("""
            INSERT INTO users(email, username, bio, image, password_hash)
            VALUES(:email, :username, :bio, :image, :passwordHash)
            """)
            .setParameter("email", ul.user().getEmail())
            .setParameter("username", ul.user().getUsername())
            .setParameter("bio", ul.user().getBio())
            .setParameter("image", ul.user().getImage())
            .setParameter("password", ul.login().password())
            .executeUpdate());
  }

  private Response post(Login login) {
    return webTarget.request("application/json").post(Entity.json(login));
  }

  private void cleanDatabase() {
    entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
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
  void setupDatabase() {
    cleanDatabase();
  }

  @AfterEach
  void tearDownDatabase() {
    cleanDatabase();
  }

  @DisplayName("Login should return user email")
  @MethodSource("getLogins")
  @ParameterizedTest
  void login0(UserWithLogin ul) {
    createUser(ul);
    Response response = post(ul.login());

    Assertions.assertEquals(200, response.getStatus(), "status");
    Assertions.assertTrue(response.hasEntity(), "has entity");
    Assertions.assertAll(
        () -> {
          JsonObject responseBody = response.readEntity(JsonObject.class);

          Assertions.assertTrue(responseBody.containsKey("user"), "contains user");
          Assertions.assertAll(
              () -> {
                JsonObject user = responseBody.getJsonObject("user");
                String userEmail = ul.login().email();

                Assertions.assertEquals(userEmail, user.getString("email"), "email");
              });
        });
  }

  @DisplayName("Login should return user properties")
  @MethodSource("getLoginProperties")
  @ParameterizedTest
  void login2(String property, UserWithLogin ul) {
    createUser(ul);
    Response response = post(ul.login());

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
  @MethodSource("getLogins")
  @ParameterizedTest
  void login_reject(UserWithLogin ul) {
    var response = post(ul.login());

    Assertions.assertEquals(401, response.getStatus(), "status");
  }

  private record UserWithLogin(User user, Login login) {}
}
