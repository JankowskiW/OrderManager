package pl.wj.ordermanager.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pl.wj.ordermanager.security.user.model.User;
import pl.wj.ordermanager.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

import static pl.wj.ordermanager.util.JwtUtil.TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
public class RestAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(
                user.getUsername(),
                authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()),
                request.getRequestURL().toString());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), request.getRequestURL().toString());
        response.setHeader("access_token", TOKEN_PREFIX + accessToken);
        response.setHeader("refresh_token", TOKEN_PREFIX + refreshToken);
    }
}
