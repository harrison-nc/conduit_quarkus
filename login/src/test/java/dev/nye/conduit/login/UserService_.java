package dev.nye.conduit.login;

import dev.nye.conduit.login.user.User;
import dev.nye.conduit.login.user.UserResponse;
import dev.nye.conduit.login.user.UserService;
import io.quarkus.test.Mock;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.Dependent;
import javax.json.Json;
import javax.json.JsonObject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jose4j.base64url.Base64;

@RestClient
@Dependent
@Mock
public class UserService_ implements UserService {
  private final Map<String, User> users = new HashMap<>();

  {
    users.put("test@mail.com", new User("test@mail.com", "test", "A test user", "test.jpeg"));
    users.put("john@mail.com", new User("john@mail.com", "john", "A john user", "john.jpeg"));
  }

  private String getJwtUpnClaim(String token) {
    String[] chunks = token.split("\\.");

    assert chunks.length == 3;

    String claimsEncoded = chunks[1];

    byte[] claimsByte = Base64.decode(claimsEncoded);
    String claimsString = new String(claimsByte);
    JsonObject claims = Json.createReader(new StringReader(claimsString)).readObject();

    assert claims.containsKey("upn");

    return claims.getString("upn");
  }

  @Override
  public UserResponse getUser(String token) {
    return new UserResponse(users.get(getJwtUpnClaim(token)));
  }
}
