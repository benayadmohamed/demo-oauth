package com.config.security;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.model.sec.Privilege;
import com.model.sec.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;

import static com.config.security.SecurityConstants.*;

@Component
public class JwtOutils {
    @Value("${jwt.secret:secret}")
    private String secret;
    @Value("${jwt.expiration:9000000}")
    private long expiration;


    public DecodedJWT verify(String token) throws JWTVerificationException {
        return JWT.require(
                Algorithm.HMAC512(secret.getBytes())
            )
            .build()
            .verify(token.replace(TOKEN_PREFIX, ""));
    }

    public String create(User user) throws JWTVerificationException {
        Collection<Privilege> authorities = user.getAuthorities();
        String[] privileges = authorities.stream()
            .map(privilege -> privilege.getAuthority())
            .distinct().toArray(String[]::new);
        return JWT.create()
            .withSubject(user.getUsername())
            .withArrayClaim(PRIVILEGES, privileges)
            .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
            .sign(Algorithm.HMAC512(secret.getBytes()));
    }
}
