package dev.nye.conduit.common.impl;

import dev.nye.conduit.common.JwtGenerator;
import io.smallrye.jwt.build.Jwt;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class JwtGeneratorImpl implements JwtGenerator {

    @Override
    public String generateJwt(Map<String, Object> m) {
        String token = Jwt.claims(m).sign();
        System.out.println(token);
        return token;
    }
}
