package pl.wj.ordermanager.security.user;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.security.user.model.User;
import pl.wj.ordermanager.security.user.model.UserMapper;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.security.user.model.dto.UserUpdateRequestDto;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            role.getPrivileges().forEach(privilege -> authorities.add(new SimpleGrantedAuthority(privilege.getName())));
        } );
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }

    public User getUserByUsername(String username) {
        return userRepository.getByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    public UserResponseDto addUser(UserRequestDto userRequestDto) {
        long loggedUserId = userRepository.getLoggedUserId()
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
        userRequestDto.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        user.setCreatedBy(loggedUserId);
        user.setUpdatedBy(loggedUserId);
        return userMapper.userToUserResponseDto(userRepository.save(user));
    }

    public UserResponseDto editUser(UserUpdateRequestDto userUpdateRequestDto) {
        throw new NotYetImplementedException();
    }

    public Page<UserResponseDto> getUsers(Boolean archived, Pageable pageable) {
        if (archived == null) return userRepository.getUsers(pageable);
        return userRepository.getUsers(archived, pageable);
    }
}
