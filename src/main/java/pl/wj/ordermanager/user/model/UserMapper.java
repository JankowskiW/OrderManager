package pl.wj.ordermanager.user.model;

import org.mapstruct.Mapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.user.model.dto.UserUpdateRequestDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userRequestDtoToUser(UserRequestDto userRequestDto);
    UserResponseDto userToUserResponseDto(User user);
    UserRequestDto userToUserRequestDto(User user);
    User userUpdateRequestDtoToUser(UserUpdateRequestDto userUpdateRequestDto);
    UserUpdateRequestDto userToUserUpdateRequestDto(User exampleUser);
    UserResponseDto userUpdateRequestDtoToUserResponseDto(UserUpdateRequestDto userUpdateRequestDto);
    default User userUpdateRequestDtoToUser(UserUpdateRequestDto userUpdateRequestDto, User user) {
        if (!userUpdateRequestDto.getUsername().isBlank()) user.setUsername(userUpdateRequestDto.getUsername());
        if (!userUpdateRequestDto.getFirstName().isBlank()) user.setFirstName(userUpdateRequestDto.getFirstName());
        if (!userUpdateRequestDto.getLastName().isBlank()) user.setLastName(userUpdateRequestDto.getLastName());
        if (!userUpdateRequestDto.getEmailAddress().isBlank()) user.setEmailAddress(userUpdateRequestDto.getEmailAddress());
        return  user;
    }
}
