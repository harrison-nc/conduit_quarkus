package dev.nye.conduit.login;

public sealed interface LoginResponse {

    record User(String email, String username, String bio) {

        public User(String email) {
            this(email, "", "");
        }
    }

    record Success(User user) implements LoginResponse {}

    record Failure(String msg) implements LoginResponse {

        public Failure() {
            this("");
        }
    }
}
