package pl.wj.ordermanager.security.registration;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.security.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.security.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.security.user.UserService;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final ConfirmationTokenService confirmationTokenService;
    private final UserService userService;

    public UserResponseDto registerUser(UserRequestDto userRequestDto) {
        return userService.addUser(userRequestDto);
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
