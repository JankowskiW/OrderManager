package pl.wj.ordermanager.registration;

import org.mapstruct.factory.Mappers;
import pl.wj.ordermanager.user.model.User;
import pl.wj.ordermanager.user.model.UserMapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

import java.time.LocalDateTime;

public class RegistrationServiceTestHelper {

    private static LocalDateTime currentTimestamp = LocalDateTime.now();
    private static UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private static String subject = "Company - Email confirmation";
    private static String confirmationLink = "http://localhost:8080/api/registration/confirm?token=";

    static LocalDateTime getCurrentTimestamp() {
        return currentTimestamp;
    }
    static String getSubject() {return subject;}
    static String getConfirmationLink() {return confirmationLink;}

    static UserRequestDto createExampleUserRequestDto() {
        return UserRequestDto.builder()
                .username("johndoe")
                .firstName("John")
                .lastName("Doe")
                .emailAddress("jdoe@example.com")
                .password("password")
                .build();
    }

    static UserResponseDto createExampleUserResponseDto(User user) {
        return userMapper.userToUserResponseDto(user);
    }

    public static User createExampleUser(UserRequestDto userRequestDto, long createdBy, long newUserId) {
        User user = userMapper.userRequestDtoToUser(userRequestDto);
        user.setId(newUserId);
        user.setCreatedBy(createdBy);
        user.setCreatedAt(currentTimestamp);
        user.setUpdatedBy(createdBy);
        user.setCreatedAt(currentTimestamp);
        return user;
    }
}
