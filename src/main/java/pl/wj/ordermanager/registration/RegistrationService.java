package pl.wj.ordermanager.registration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.email.EmailSender;
import pl.wj.ordermanager.user.UserService;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;
    private final EmailSender emailSender;
    private final UserMapper userMapper;
    private final long confirmationTokenExpirationTime;
    private final String senderEmailAddress;

    public RegistrationService(ConfirmationTokenService confirmationTokenService,
                               UserService userService,
                               EmailSender emailSender,
                               UserMapper userMapper,
                               @Value("${confirmation-token.register.expiration-time}") Long confirmationTokenExpirationTime,
                               @Value("${spring.mail.username}") String senderEmailAddress) {
        this.confirmationTokenService = confirmationTokenService;
        this.userService = userService;
        this.emailSender = emailSender;
        this.userMapper = userMapper;
        this.confirmationTokenExpirationTime = confirmationTokenExpirationTime;
        this.senderEmailAddress = senderEmailAddress;
    }

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        User user = userService.addUser(userRequestDto);
        String confirmationToken = createNewConfirmationToken(user);
        sendVerificationEmailMessage(user, confirmationToken);
        return userMapper.userToUserResponseDto(user);
    }

    private String createNewConfirmationToken(User user) {
        ConfirmationToken confirmationToken = new ConfirmationToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(confirmationTokenExpirationTime),
                user
        );
        return confirmationTokenService.addConfirmationToken(confirmationToken).getToken();
    }

    private void sendVerificationEmailMessage(User user, String confirmationToken) {
        String subject = "Company - Email confirmation";
        String confirmationLink = "http://localhost:8080/api/registration/confirm?token=" + confirmationToken;
        emailSender.sendRegistrationConfirmationToken(senderEmailAddress, user.getEmailAddress(), subject,
                user.getUsername(), confirmationLink, confirmationTokenExpirationTime);
    }

    @Transactional
    public String confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);
        validateConfirmationToken(confirmationToken);
        confirmEmailAddress(confirmationToken);
        return "Email address confirmed";
    }

    private void validateConfirmationToken(ConfirmationToken confirmationToken) throws RuntimeException {
        if (confirmationToken.getConfirmedAt() != null) throw new RuntimeException("Email already confirmed");
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) throw new RuntimeException("Token expired");
    }

    private void confirmEmailAddress(ConfirmationToken confirmationToken) {
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenService.updateConfirmationToken(confirmationToken);
        userService.enableUser(confirmationToken.getUser());
    }
}
