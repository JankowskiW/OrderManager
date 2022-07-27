package pl.wj.ordermanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wj.ordermanager.security.role.model.Role;
import pl.wj.ordermanager.security.user.UserService;
import pl.wj.ordermanager.security.user.model.User;
import pl.wj.ordermanager.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static pl.wj.ordermanager.util.JwtUtil.TOKEN_PREFIX;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader(AUTHORIZATION);
        String username = jwtUtil.extractUsernameFromRefreshToken(refreshToken, true);
        if (username != null) {
            User user = userService.getUserByUsername(username);
            String accessToken = jwtUtil.generateAccessToken(
                    username,
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
                    request.getRequestURL().toString());
            response.setHeader("access_token", TOKEN_PREFIX + accessToken);
            response.setHeader("refresh_token", refreshToken);
        }
    }
}
