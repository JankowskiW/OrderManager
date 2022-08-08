package pl.wj.ordermanager.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.wj.ordermanager.user.UserService;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.util.JwtUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static pl.wj.ordermanager.security.SecurityServiceTestHelper.*;
import static pl.wj.ordermanager.util.JwtUtil.TOKEN_PREFIX;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private SecurityService securityService;

    @Test
    @DisplayName("Should refresh access token")
    void shouldRefreshAccessToken() {
        // given
        String username = "user";
        String issuer = "issuer";
        User user = createExampleUser(username);
        String refreshToken = generateRefreshToken(username, issuer);
        String expectedAccessToken = generateAccessToken(username, issuer);
        given(jwtUtil.extractUsernameFromRefreshToken(anyString(), anyBoolean())).willReturn(username);
        given(userService.getUserByUsername(anyString())).willReturn(user);
        given(jwtUtil.generateAccessToken(anyString(), anyList(), anyString())).willReturn(expectedAccessToken);

        // when
        String accessToken = securityService.refreshAccessToken(refreshToken, issuer);

        // then
        assertThat(accessToken)
                .isNotEmpty()
                .isEqualTo(TOKEN_PREFIX + expectedAccessToken);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException when refresh token does not contain username")
    void shouldThrowExceptionWhenRefreshTokenDoesNotContainUsername() {
        // given
        String username = "user";
        String issuer = "issuer";
        String refreshToken = generateRefreshToken(username, issuer);
        given(jwtUtil.extractUsernameFromRefreshToken(anyString(), anyBoolean())).willReturn(null);

        // when
        assertThatThrownBy(() -> securityService.refreshAccessToken(refreshToken, issuer))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Username cannot be null");
    }
}