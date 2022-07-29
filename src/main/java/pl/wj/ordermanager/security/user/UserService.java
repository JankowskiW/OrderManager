package pl.wj.ordermanager.security.user;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.security.confirmationtoken.ConfirmationTokenService;
import pl.wj.ordermanager.security.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.security.user.model.User;
import pl.wj.ordermanager.security.user.model.UserMapper;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.security.user.model.dto.UserUpdateRequestDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final ConfirmationTokenService confirmationTokenService;
    private final PasswordEncoder passwordEncoder;
    private final long confirmationTokenExpirationTime;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public UserService(UserRepository userRepository,
                       ConfirmationTokenService confirmationTokenService,
                       PasswordEncoder passwordEncoder,
                       @Value("${confirmation-token.expiration-time}") long confirmationTokenExpirationTime) {
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.passwordEncoder = passwordEncoder;
        this.confirmationTokenExpirationTime = confirmationTokenExpirationTime;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    public User getUserByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    @Transactional
    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        long loggedUserId = userRepository.getLoggedUserId()
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
        userRequestDto.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        user.setCreatedBy(loggedUserId);
        user.setUpdatedBy(loggedUserId);
        user = userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(confirmationTokenExpirationTime),
                user
        );
        confirmationTokenService.addConfirmationToken(confirmationToken);
        return userMapper.userToUserResponseDto(user);
    }

    public UserResponseDto editUser(UserUpdateRequestDto userUpdateRequestDto) {
        throw new NotYetImplementedException();
    }

    public Page<UserResponseDto> getUsers(Boolean archived, Pageable pageable) {
        if (archived == null) return userRepository.getUsers(pageable);
        return userRepository.getUsers(archived, pageable);
    }

    public void enableUser(User user) {
        user.setEnabled(true);
        userRepository.save(user);
    }
}
