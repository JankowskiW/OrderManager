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
}
