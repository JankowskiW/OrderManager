package pl.wj.ordermanager.security.user.model;

import org.mapstruct.Mapper;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;

@Mapper
public interface UserMapper {
    User userRequestDtoToUser(UserRequestDto userRequestDto);
    User userResponseDtoToUser(UserResponseDto userResponseDto);
    UserResponseDto userToUserResponseDto(User user);

}
