package pl.wj.ordermanager.user;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.security.core.userdetails.UserDetails;
import pl.wj.ordermanager.role.model.Role;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserServiceTestHelper {

    private static final long NUMBER_OF_USERS = 10;

    private static LocalDateTime currentTimestamp = LocalDateTime.now();
    private static UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    static List<UserResponseDto> createListOfUserResponseDtos() {
        List<UserResponseDto> userResponseDtos = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_USERS; i++) {
            User user = createExampleUser(false,i+1);
            if (i%2 == 0) archivizeExampleUser(user);
            userResponseDtos.add(userMapper.userToUserResponseDto(user));
        }
        return userResponseDtos;
    }

    private static void archivizeExampleUser(User user) {
        user.setArchivedBy(1L);
        user.setArchivedAt(currentTimestamp);
    }

    static LocalDateTime getCurrentTimestamp() {
        return currentTimestamp;
    }

    static User mapUserRequestDtoToUser(UserRequestDto userRequestDto) {
        return userMapper.userRequestDtoToUser(userRequestDto);
    }

    static UserDetails createExampleUserDetails(User user) {
        return user;
    }

    static UserRequestDto createExampleUserRequestDto(User user) {
        return userMapper.userToUserRequestDto(user);
    }

    static User createExampleUser(boolean withRoles, long id) {
        User user = new User();
        user.setId(id);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");
        user.setEmailAddress("jdoe@example.com");
        user.setPassword("Password");
        user.setCreatedBy(1L);
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedBy(1L);
        user.setUpdatedAt(currentTimestamp);
        user.setLocked(false);
        user.setEnabled(false);
        if (withRoles) {
            List<Role> userRoles = new ArrayList<>();
            userRoles.add(createExampleRole());
            user.setRoles(userRoles);
        }
        return user;
    }

    private static Role createExampleRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_ADMIN");
        role.setDescription("Administrator");
        role.setCreatedBy(1L);
        role.setCreatedAt(currentTimestamp);
        role.setUpdatedBy(1L);
        role.setUpdatedAt(currentTimestamp);
        role.setPrivileges(new ArrayList<>());
        return role;
    }
}
