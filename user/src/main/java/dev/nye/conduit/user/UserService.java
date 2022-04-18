package dev.nye.conduit.user;

import java.util.Optional;

public interface UserService {

  Optional<User> getUser(String email);

  Optional<User> updateUser(User user);
}
