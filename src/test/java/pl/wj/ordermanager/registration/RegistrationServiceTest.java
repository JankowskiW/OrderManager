package pl.wj.ordermanager.registration;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import pl.wj.ordermanager.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.email.EmailSender;
import pl.wj.ordermanager.user.UserService;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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

    private static final long TOKEN_EXPIRATION_TIME = 15;
    private static final String SENDER_EMAIL_ADDRESS = "example@example.com";

    @BeforeEach
    void setUp() {
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
        long createdBy = 1L;
        long newUserId = 2L;
        UserRequestDto userRequestDto = createExampleUserRequestDto();
        User user = createExampleUser(userRequestDto, createdBy, newUserId);
        UserResponseDto expectedResponse = createExampleUserResponseDto(user);

        given(userService.addUser(any(UserRequestDto.class))).willReturn(user);
        willDoNothing().given(emailSender).send(
                anyString(), anyString(), anyString(),
                anyString(), anyString(), anyLong());

        // when
        UserResponseDto userResponseDto = registrationService.registerUser(userRequestDto);

        // then
        assertThat(userResponseDto)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }
}