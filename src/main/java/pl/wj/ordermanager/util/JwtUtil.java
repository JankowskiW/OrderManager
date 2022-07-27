package pl.wj.ordermanager.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    public static final String TOKEN_PREFIX = "Bearer ";

    private final long ACCESS_TOKEN_EXP_TIME;
    private final long REFRESH_TOKEN_EXP_TIME;
    private final Algorithm ALGORITHM;

    public JwtUtil (
            @Value("${jwt.access-token-expiration-time}") long accessTokenExpTime,
            @Value("${jwt.refresh-token-expiration-time}") long refreshTokenExpTime,
            @Value("${jwt.secret}") String secret) {
        ACCESS_TOKEN_EXP_TIME = accessTokenExpTime;
        REFRESH_TOKEN_EXP_TIME = refreshTokenExpTime;
        ALGORITHM = Algorithm.HMAC256(secret.getBytes());
    }

    public String generateAccessToken(String username, List<String> authorities, String issuer) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username is not valid.");
        }
        if (authorities == null) {
            throw new RuntimeException("Cannot pass null as authorities list.");
        }
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXP_TIME))
                .withClaim("roles", authorities);

        if (issuer != null) {
            jwtBuilder = jwtBuilder.withIssuer(issuer);
        }
        return jwtBuilder.sign(ALGORITHM);
    }

    public String generateRefreshToken(String username, String issuer) {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username is not valid.");
        }
        JWTCreator.Builder jwtBuilder = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP_TIME));

        if (issuer != null) {
            jwtBuilder = jwtBuilder.withIssuer(issuer);
        }
        return jwtBuilder.sign(ALGORITHM);
    }

    public String extractUsernameFromRefreshToken(String refreshToken, boolean isBearer) {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is missing");
        }
        if (isBearer && !refreshToken.startsWith(TOKEN_PREFIX)) {
            throw new RuntimeException("Refresh token has invalid prefix");
        }
        return JWT.require(ALGORITHM)
                .build()
                .verify(refreshToken.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }
}
