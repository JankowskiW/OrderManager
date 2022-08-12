package pl.wj.ordermanager.registration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wj.ordermanager.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.email.EmailSender;
import pl.wj.ordermanager.user.UserService;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;
import static pl.wj.ordermanager.registration.RegistrationServiceTestHelper.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private UserService userService;
    @Mock
    private EmailSender emailSender;

    private RegistrationService registrationService;

    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
        registrationService = new RegistrationService(
                confirmationTokenService,
                userService,
                emailSender,
                Mappers.getMapper(UserMapper.class),
                TOKEN_EXPIRATION_TIME,
                SENDER_EMAIL_ADDRESS
        );
    }

    @Test
    @DisplayName("Should register new user and send confirmation token")
    void shouldRegisterNewUserAndSendConfirmationToken() {
        // given
        long tokenId = 1L;
        long createdBy = 1L;
        long newUserId = 2L;
        UserRequestDto userRequestDto = createExampleUserRequestDto();
        User user = createExampleUser(userRequestDto, createdBy, newUserId);
        UserResponseDto expectedResponse = createExampleUserResponseDto(user);

        given(userService.addUser(any(UserRequestDto.class))).willReturn(user);
        given(confirmationTokenService.addConfirmationToken(any(ConfirmationToken.class))).willAnswer(
                i -> {
                    ConfirmationToken ct = i.getArgument(0, ConfirmationToken.class);
                    ct.setId(tokenId);
                    return ct;
                });
        willDoNothing().given(emailSender).sendRegistrationConfirmationToken(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyLong());

        // when
        UserResponseDto userResponseDto = registrationService.registerUser(userRequestDto);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
        verify(emailSender).sendRegistrationConfirmationToken(
                eq(SENDER_EMAIL_ADDRESS), eq(user.getEmailAddress()), eq(getSubject()),
                eq(user.getUsername()), startsWith(getConfirmationLink()), eq(TOKEN_EXPIRATION_TIME));
    }

    @Test
    @DisplayName("Should confirm email using confirmation token")
    void shouldConfirmEmailUsingConfirmationToken() {
        // given
        ConfirmationToken confirmationToken = createExampleConfirmationToken(false, false);
        ConfirmationToken confirmedConfirmationToken = createExampleConfirmationToken(true, false);
        String expectedResponse = "Email address confirmed";
        given(confirmationTokenService.getConfirmationToken(anyString())).willReturn(confirmationToken);
        given(confirmationTokenService.updateConfirmationToken(any(ConfirmationToken.class))).willReturn(confirmedConfirmationToken);
        willDoNothing().given(userService).enableUser(any(User.class));

        // when
        String response = registrationService.confirmEmail(confirmationToken.getToken());

        // then
        assertThat(response)
                .isNotEmpty()
                .isEqualTo(expectedResponse);
        verify(confirmationTokenService).updateConfirmationToken(confirmationToken);
        verify(userService).enableUser(confirmationToken.getUser());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when token does not exist in database")
    void shouldThrowExceptionWhenTokenDoesNotExistInDatabase() {
        // given
        String exampleToken = "exampleToken";
        given(confirmationTokenService.getConfirmationToken(anyString()))
                .willAnswer(i -> { throw new RuntimeException("Token not found"); });

        // when
        assertThatThrownBy(() -> registrationService.confirmEmail(exampleToken))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token not found");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when email already confirmed")
    void shouldThrowExceptionWhenEmailAlreadyConfirmed() {
        // given
        ConfirmationToken confirmationToken = createExampleConfirmationToken(true, false);
        given(confirmationTokenService.getConfirmationToken(anyString())).willReturn(confirmationToken);

        // when
        assertThatThrownBy(() -> registrationService.confirmEmail(confirmationToken.getToken()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already confirmed");
    }

    @Test
    @DisplayName("Should throw InvalidTokenException when confirmation token expired")
    void shouldThrowExceptionWhenTokenWhenConfirmationTokenExpired() {
        // given
        ConfirmationToken confirmationToken = createExampleConfirmationToken(false, true);
        given(confirmationTokenService.getConfirmationToken(anyString())).willReturn(confirmationToken);

        // when
        assertThatThrownBy(() -> registrationService.confirmEmail(confirmationToken.getToken()))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token expired");
    }
}