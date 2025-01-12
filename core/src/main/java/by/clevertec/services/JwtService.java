package by.clevertec.services;

import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    boolean isTokenValid(String token);
}
