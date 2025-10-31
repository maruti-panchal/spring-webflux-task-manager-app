package com.learnspring.webfluxtaskmanagerapp.service;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtServiceIMPL implements JwtService {
    private final Environment env;
    public JwtServiceIMPL(Environment env) {

        this.env = env;

    }
    @Override
    public String generateJwt(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public Mono<Boolean> validateJwt(String token) {
        return Mono.just(token)
                .map(jwt->getClaimsFromJwt(jwt))
                .map(claims -> !claims.getExpiration().before(new Date()))
                .onErrorReturn(false);
    }

    @Override
    public String extractTokenSubject(String token) {
        return getClaimsFromJwt(token).getSubject();
    }

    private Claims getClaimsFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSecretKey() {
        return Optional.ofNullable(env.getProperty("token.secret"))
                .map(secretToken->secretToken.getBytes())
                .map(tokenScretByte-> Keys.hmacShaKeyFor(tokenScretByte))
                .orElseThrow(()->new RuntimeException("Could not find SecretKey"));
    }
}
