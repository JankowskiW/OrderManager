package pl.wj.ordermanager.user;

import lombok.AllArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.exception.ResourceExistsException;
import pl.wj.ordermanager.exception.ResourceNotFoundException;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserPasswordDto;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.user.model.dto.UserUpdateRequestDto;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username)  {
        return getUserByUsername(username);
    }

    public User getUserByUsername(String username)  {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    @Transactional
    public User addUser(UserRequestDto userRequestDto) {
        if(userRepository.existsByUsernameOrEmailAddress(userRequestDto.getUsername(), userRequestDto.getEmailAddress())) {
            throw new ResourceExistsException("user", "username or email address");
        }
        long loggedInUserId = getLoggedInUserId();
        User user = mapUserRequestDtoWithAuditFieldsToUser(userRequestDto, loggedInUserId);
        encodeUserPassword(user);
        return userRepository.save(user);
    }

    private void encodeUserPassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private User mapUserRequestDtoWithAuditFieldsToUser(UserRequestDto userRequestDto, long loggedInUserId) {
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        user.setCreatedBy(loggedInUserId);
        user.setUpdatedBy(loggedInUserId);
        return user;
    }

    public UserResponseDto editUser(long id, UserUpdateRequestDto userUpdateRequestDto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user"));
        user = mapUserUpdateRequestDtoWithAuditFieldsAndIdToUser(id, userUpdateRequestDto, user);
        return userMapper.userToUserResponseDto(userRepository.save(user));
    }

    private User mapUserUpdateRequestDtoWithAuditFieldsAndIdToUser(long id, UserUpdateRequestDto userUpdateRequestDto, User user) {
        user = userMapper.userUpdateRequestDtoToUser(userUpdateRequestDto, user);
        user.setId(id);
        user.setUpdatedBy(getLoggedInUserId());
        return user;
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
        throw new NotYetImplementedException();
    }
}
