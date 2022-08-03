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
        validateAuthorities(authorities);
        validateUsername(username);
        JWTCreator.Builder jwtBuilder = createJwtBuilder(username, "roles", authorities);
        setIssuerToJwtBuilder(jwtBuilder, issuer);
        return jwtBuilder.sign(ALGORITHM);
    }

    private void validateAuthorities(List<String> authorities) throws RuntimeException {
        if (authorities == null) {
            throw new RuntimeException("Cannot pass null as authorities list.");
        }
    }

    private JWTCreator.Builder createJwtBuilder(String username, String claimName, List<String> authorities) {
        return createJwtBuilder(username).withClaim(claimName, authorities);
    }

    public String generateRefreshToken(String username, String issuer) {
        validateUsername(username);
        JWTCreator.Builder jwtBuilder = createJwtBuilder(username);
        setIssuerToJwtBuilder(jwtBuilder, issuer);
        return jwtBuilder.sign(ALGORITHM);
    }

    private void validateUsername(String username) throws RuntimeException {
        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username is not valid.");
        }
    }

    private JWTCreator.Builder createJwtBuilder(String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXP_TIME));
    }

    private void setIssuerToJwtBuilder(JWTCreator.Builder jwtBuilder, String issuer) {
        if (issuer != null) jwtBuilder.withIssuer(issuer);
    }

    public String extractUsernameFromRefreshToken(String refreshToken, boolean isBearer) {
        validateRefreshToken(refreshToken, isBearer);
        return getSubjectFromRefreshToken(refreshToken);
    }

    private void validateRefreshToken(String refreshToken, boolean isBearer) throws RuntimeException {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token is missing");
        }
        if (isBearer && !refreshToken.startsWith(TOKEN_PREFIX)) {
            throw new RuntimeException("Refresh token has invalid prefix");
        }
    }

    private String getSubjectFromRefreshToken(String refreshToken) {
        return JWT.require(ALGORITHM)
                .build()
                .verify(refreshToken.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }
}
