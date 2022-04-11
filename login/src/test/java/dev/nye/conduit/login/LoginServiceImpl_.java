package dev.nye.conduit.login;

import dev.nye.conduit.login.impl.LoginServiceImpl;
import dev.nye.conduit.login.user.User;
import dev.nye.conduit.login.user.UserResponse;
import dev.nye.conduit.login.user.UserService;
import io.quarkus.test.Mock;
import org.jose4j.base64url.Base64;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.persistence.EntityManager;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
@Mock
public class LoginServiceImpl_ implements LoginService {

    @Inject
    EntityManager entityManager;

    @Inject
    LoginMapper mapper;

    LoginService loginService;

    @PostConstruct
    public void init() {
        UserService userService = new UserService() {
            private final Map<String, User> users = new HashMap<>();

            {
                users.put("test", new User("test@mail.com", "test", "A test user", "test.jpeg"));
                users.put("john", new User("john@mail.com", "john", "A john user", "john.jpeg"));
            }

            private String getJwtEmailClaim(String token) {
                String[] chunks = token.split("\\.");

                assert  chunks.length == 3;

                String claimsEncoded = chunks[1];

                byte[] claimsByte = Base64.decode(claimsEncoded);
                String claimsString = new String(claimsByte);
                JsonObject claims = Json.createReader(new StringReader(claimsString)).readObject();

                assert claims.containsKey("email");

                return claims.getString("email");
            }

            @Override
            public UserResponse getUser(String token) {
                System.out.println("\n".repeat(10));
                System.out.println("# Using test user service");
                System.out.println("\n".repeat(10));
                return new UserResponse(users.get(getJwtEmailClaim(token)));
            }
        };

        loginService = new LoginServiceImpl(entityManager, mapper, userService);
    }


    @Override
    public Optional<User> login(LoginRequest req) {
        return loginService.login(req);
    }
}
