package pl.wj.ordermanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.wj.ordermanager.user.model.dto.UserCredentialsDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
public class SecurityController {
    private final SecurityService securityService;

    @PostMapping("/login")
    public void login(@RequestBody UserCredentialsDto credentials) {
    }

    @GetMapping("/security/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = request.getHeader(AUTHORIZATION);
        String accessToken = securityService.refreshAccessToken(refreshToken, request.getRequestURL().toString());
        setResponseHeaders(response, accessToken, refreshToken);
    }

    private void setResponseHeaders(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setHeader("access_token", accessToken);
        response.setHeader("refresh_token", refreshToken);
    }
}
