package pl.wj.ordermanager.security.registration;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.security.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.security.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.security.email.EmailSender;
import pl.wj.ordermanager.security.user.UserService;
import pl.wj.ordermanager.security.user.model.User;
import pl.wj.ordermanager.security.user.model.UserMapper;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RegistrationService {

    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;
    private final EmailSender emailSender;
    private final long confirmationTokenExpirationTime;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public RegistrationService(ConfirmationTokenService confirmationTokenService,
                               UserService userService,
                               EmailSender emailSender,
                               @Value("${confirmation-token.expiration-time}") long confirmationTokenExpirationTime) {
        this.confirmationTokenService = confirmationTokenService;
        this.userService = userService;
        this.emailSender = emailSender;
        this.confirmationTokenExpirationTime = confirmationTokenExpirationTime;
    }

    @Transactional
    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        User user = userService.addUser(userRequestDto);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(confirmationTokenExpirationTime),
                user
        );
        confirmationTokenService.addConfirmationToken(confirmationToken);


        String from = "developmentwj@gmail.com";
        String subject = "Company - Email confirmation";
        String confirmationLink = "http://localhost:8080/api/registration/confirm?token=" + confirmationToken.getToken();
        emailSender.send(from, user.getEmailAddress(), subject, user.getUsername(), confirmationLink, confirmationTokenExpirationTime);
        return userMapper.userToUserResponseDto(user);

    }

    @Transactional
    public String confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        if (confirmationToken.getConfirmedAt() != null) throw new RuntimeException("Email already confirmed");
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) throw new RuntimeException("Token expired");

        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenService.updateConfirmationToken(confirmationToken);
        userService.enableUser(confirmationToken.getUser());
        return "Email address confirmed";
    }
}
