package pl.wj.ordermanager.user.model;

import org.mapstruct.Mapper;
import org.springframework.security.core.userdetails.UserDetails;
import pl.wj.ordermanager.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userRequestDtoToUser(UserRequestDto userRequestDto);
    UserResponseDto userToUserResponseDto(User user);
    UserRequestDto userToUserRequestDto(User user);
    UserResponseDto userRequestDtoToUserResponseDto(UserRequestDto userRequestDto);
}
