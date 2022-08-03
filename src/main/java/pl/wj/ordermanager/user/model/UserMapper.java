package pl.wj.ordermanager.user.model;

import org.mapstruct.Mapper;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userRequestDtoToUser(UserRequestDto userRequestDto);
    User userResponseDtoToUser(UserResponseDto userResponseDto);
    UserResponseDto userToUserResponseDto(User user);

}
