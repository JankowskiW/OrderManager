package pl.wj.ordermanager.security;

import pl.wj.ordermanager.user.UserServiceTestHelper;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.util.JwtUtil;

import java.util.ArrayList;
import java.util.List;

public class SecurityServiceTestHelper {

    private static final long ACCESS_TOKEN_EXP_TIME = 450000;
    private static final long REFRESH_TOKEN_EXP_TIME = 900000;
    private static final String SECRET = "secret";

    private static final JwtUtil jwtUtil = new JwtUtil(ACCESS_TOKEN_EXP_TIME, REFRESH_TOKEN_EXP_TIME, SECRET);

    public static User createExampleUser(String username) {
        return UserServiceTestHelper.createExampleUser(true, username);
    }

    public static String generateAccessToken(String username, String issuer) {
        return jwtUtil.generateAccessToken(username, new ArrayList<>(), issuer);
    }

    public static String generateRefreshToken(String username, String issuer) {
        return jwtUtil.generateRefreshToken(username, issuer);
    }
}
