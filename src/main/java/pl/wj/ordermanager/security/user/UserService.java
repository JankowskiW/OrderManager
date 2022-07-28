package pl.wj.ordermanager.security.user;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.security.user.model.User;
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
        // TODO: 27.07.2022 : Create userMapper and method to map UserRequestDto to User
        User user = new User();
        user.setId(userRequestDto.getId());
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setCreatedBy(userRequestDto.getCreatedBy());
        user.setCreatedAt(userRequestDto.getCreatedAt());
        user.setUpdatedBy(userRequestDto.getUpdatedBy());
        user.setUpdatedAt(userRequestDto.getUpdatedAt());


        // TODO: 28.07.2022 : Create userMapper and mathod to map User to UserResponseDto
        return UserResponseDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .createdBy(user.getCreatedBy())
                .createdAt(user.getCreatedAt())
                .updatedBy(user.getUpdatedBy())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    public UserResponseDto editUser(UserUpdateRequestDto userUpdateRequestDto) {
        throw new NotYetImplementedException();
    }
}
