package pl.wj.ordermanager.user;

import lombok.AllArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.user.model.dto.UserUpdateRequestDto;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByUsername(username);
    }

    public User getUserByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    @Transactional
    public User addUser(UserRequestDto userRequestDto) {
        long loggedInUserId = getLoggedInUserId();
        encodeUserPassword(userRequestDto);
        User user = mapUserRequestDtoWithAuditFieldsToUser(userRequestDto, loggedInUserId);
        return userRepository.save(user);
    }

    private long getLoggedInUserId() throws UsernameNotFoundException {
        return userRepository.getLoggedInUserId()
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    private void encodeUserPassword(UserRequestDto userRequestDto) {
        userRequestDto.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
    }

    private User mapUserRequestDtoWithAuditFieldsToUser(UserRequestDto userRequestDto, long loggedInUserId) {
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        user.setCreatedBy(loggedInUserId);
        user.setUpdatedBy(loggedInUserId);
        return user;
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
