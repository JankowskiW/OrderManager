package pl.wj.ordermanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.role.model.Role;
import pl.wj.ordermanager.user.UserService;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.util.JwtUtil;

import java.util.stream.Collectors;

import static pl.wj.ordermanager.util.JwtUtil.TOKEN_PREFIX;

@RequiredArgsConstructor
@Service
public class SecurityService {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public String refreshAccessToken(String refreshToken, String issuer) {
        String username = jwtUtil.extractUsernameFromRefreshToken(refreshToken, true);
        validateUsername(username);
        User user = userService.getUserByUsername(username);
        String accessToken = jwtUtil.generateAccessToken(
                username,
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
                issuer);
        return TOKEN_PREFIX + accessToken;
    }

    private void validateUsername(String username) {
        if (username == null) throw new UsernameNotFoundException("Username cannot be null");
    }
}
