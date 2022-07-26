package pl.wj.ordermanager.user;

import org.mapstruct.factory.Mappers;
import org.springframework.security.core.userdetails.UserDetails;
import pl.wj.ordermanager.confirmationtoken.ConfirmationTokenServiceTestHelper;
import pl.wj.ordermanager.domain.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.exception.ExceptionHelper;
import pl.wj.ordermanager.domain.role.model.Role;
import pl.wj.ordermanager.domain.user.model.User;
import pl.wj.ordermanager.domain.user.model.UserMapper;
import pl.wj.ordermanager.domain.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.domain.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.domain.user.model.dto.UserUpdateRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserServiceTestHelper {

    private static final long NUMBER_OF_USERS = 10;
    static final long TOKEN_EXPIRATION_TIME = 5;
    static final String SENDER_EMAIL_ADDRESS = "example@example.com";


    private static String subjectPasswordReset = "Company - Reset your password";
    private static String subjectNewPassword = "Company - New password";
    private static String confirmationLink = "http://localhost:8080/api/users/password?token=";

    private static LocalDateTime currentTimestamp = LocalDateTime.now();
    private static UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    static String getSubjectPasswordReset() { return subjectPasswordReset; }
    static String getSubjectNewPassword() { return subjectNewPassword; }

    static String getConfirmationLink() {return confirmationLink;}

    static ConfirmationToken createExampleConfirmationToken(boolean confirmed, boolean expired) {
        return ConfirmationTokenServiceTestHelper.createExampleConfirmationToken(confirmed, expired);
    }

    static String createUserNotFoundMessage() {
        return ExceptionHelper.createResourceNotFoundExceptionMessage("user");
    }

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

    static UserResponseDto mapUserToUserResponseDto(User user) {
        return userMapper.userToUserResponseDto(user);
    }

    static User mapUserRequestDtoToUser(UserRequestDto userRequestDto) {
        return userMapper.userRequestDtoToUser(userRequestDto);
    }

    static User mapUserUpdateRequestDtoToUser(UserUpdateRequestDto userUpdateRequestDto) {
        return userMapper.userUpdateRequestDtoToUser(userUpdateRequestDto);
    }

    static UserResponseDto mapUserUpdateRequestDtoToUserResponseDto(UserUpdateRequestDto userUpdateRequestDto) {
        return userMapper.userUpdateRequestDtoToUserResponseDto(userUpdateRequestDto);
    }

    static UserDetails createExampleUserDetails(User user) {
        return user;
    }

    static UserRequestDto createExampleUserRequestDto(User user) {
        return userMapper.userToUserRequestDto(user);
    }

    static UserUpdateRequestDto createExampleUserUpdateRequestDto() {
        return userMapper.userToUserUpdateRequestDto(createExampleUser());
    }
    static UserUpdateRequestDto createExampleUserUpdateRequestDto(User user) {
        return userMapper.userToUserUpdateRequestDto(user);
    }

    public static User createExampleUser(boolean withRoles, String username) {
        User user = createExampleUser();
        user.setUsername(username);
        if (withRoles) {
            user.setRoles(createExampleRoles());
        }
        return user;
    }

    public static User createExampleUser(boolean withRoles, long id) {
        User user = createExampleUser();
        user.setId(id);
        if (withRoles) {
            user.setRoles(createExampleRoles());
        }
        return user;
    }

    private static List<Role> createExampleRoles() {
        List<Role> userRoles = new ArrayList<>();
        userRoles.add(createExampleRole());
        return userRoles;
    }

    private static User createExampleUser() {
        User user = new User();
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
