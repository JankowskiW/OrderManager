package pl.wj.ordermanager.domain.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.domain.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.domain.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.email.EmailSender;
import pl.wj.ordermanager.exception.ResourceExistsException;
import pl.wj.ordermanager.exception.ResourceNotFoundException;
import pl.wj.ordermanager.domain.user.model.User;
import pl.wj.ordermanager.domain.user.model.UserMapper;
import pl.wj.ordermanager.domain.user.model.dto.UserPasswordDto;
import pl.wj.ordermanager.domain.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.domain.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.domain.user.model.dto.UserUpdateRequestDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final ConfirmationTokenService confirmationTokenService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final UserMapper userMapper;
    private final long confirmationTokenExpirationTime;
    private final String senderEmailAddress;

    public UserService(ConfirmationTokenService confirmationTokenService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailSender emailSender,
                       UserMapper userMapper,
                       @Value("${confirmation-token.password-reset.expiration-time}") long confirmationTokenExpirationTime,
                       @Value("${spring.mail.username}") String senderEmailAddress) {
        this.confirmationTokenService = confirmationTokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailSender = emailSender;
        this.userMapper = userMapper;
        this.confirmationTokenExpirationTime = confirmationTokenExpirationTime;
        this.senderEmailAddress = senderEmailAddress;
    }

    @Override
    public UserDetails loadUserByUsername(String username)  {
        return getUserByUsername(username);
    }

    public User getUserByUsername(String username)  {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    @Transactional
    public User addUser(UserRequestDto userRequestDto) {
        if(userRepository.existsByUsernameOrEmailAddress(userRequestDto.getUsername(), userRequestDto.getEmailAddress())) {
            throw new ResourceExistsException("user", "username or email address");
        }

        userRequestDto.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        User user = userMapper.mapUserRequestDtoToUserWithAuditFields(userRequestDto, getLoggedInUserId());
        return userRepository.save(user);
    }

    public UserResponseDto editUser(long id, UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user"));
        user = userMapper.userUpdateRequestDtoToUserWithIdAndAuditFields(user, userUpdateRequestDto, getLoggedInUserId());
        return userMapper.userToUserResponseDto(userRepository.save(user));
    }

    private long getLoggedInUserId()  {
        return userRepository.getLoggedInUserId()
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    public Page<UserResponseDto> getUsers(Boolean archived, Pageable pageable) {
        if (archived == null) return userRepository.getUsers(pageable);
        return userRepository.getUsers(archived, pageable);
    }

    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }

    public void changePassword(UserPasswordDto userPasswordDto) {
        long loggedInUserId = getLoggedInUserId();
        User user = userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        user.setPassword(passwordEncoder.encode(userPasswordDto.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void sendPasswordResetRequest(String emailAddress) {
        User user = userRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new ResourceNotFoundException("user"));
        String confirmationToken = createNewConfirmationToken(user);
        sendPasswordResetConfirmationMessage(user, confirmationToken);
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

    private void sendPasswordResetConfirmationMessage(User user, String confirmationToken) {
        String subject = "Company - Reset your password";
        String confirmationLink = "http://localhost:8080/api/users/password?token=" + confirmationToken;
        emailSender.sendPasswordResetConfirmationToken(
               senderEmailAddress, user.getEmailAddress(), subject, confirmationLink, confirmationTokenExpirationTime);
    }

    @Transactional
    public String resetPassword(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(token);
        validateConfirmationToken(confirmationToken);
        confirmPasswordReset(confirmationToken);
        String autogeneratedPassword = generateAndChangePassword(confirmationToken.getUser());
        sendAutogeneratedPasswordMessage(confirmationToken.getUser().getEmailAddress(), autogeneratedPassword);
        return "New password was sent";
    }

    private void validateConfirmationToken(ConfirmationToken confirmationToken) throws RuntimeException {
        if (confirmationToken.getConfirmedAt() != null) throw new RuntimeException("Password already reseted");
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) throw new RuntimeException("Token expired");
    }

    private void confirmPasswordReset(ConfirmationToken confirmationToken) {
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenService.updateConfirmationToken(confirmationToken);
    }

    private String generateAndChangePassword(User user) {
        String password = UUID.randomUUID().toString().substring(0, 8);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return password;
    }

    private void sendAutogeneratedPasswordMessage(String userEmailAddress, String password) {
        String subject = "Company - New password";
        emailSender.sendAutogeneratedPassword(
                senderEmailAddress, userEmailAddress, subject, password);
    }
}
