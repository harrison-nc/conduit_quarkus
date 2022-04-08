package dev.nye.conduit.common;

import java.util.Map;

public interface JwtGenerator {

  String generateJwt(Map<String, Object> m);
}
