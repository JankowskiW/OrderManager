package pl.wj.ordermanager.domain.user.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.wj.ordermanager.domain.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.domain.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.domain.user.model.dto.UserUpdateRequestDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userRequestDtoToUser(UserRequestDto userRequestDto);
    UserResponseDto userToUserResponseDto(User user);
    UserRequestDto userToUserRequestDto(User user);
    User userUpdateRequestDtoToUser(UserUpdateRequestDto userUpdateRequestDto);
    UserUpdateRequestDto userToUserUpdateRequestDto(User exampleUser);
    UserResponseDto userUpdateRequestDtoToUserResponseDto(UserUpdateRequestDto userUpdateRequestDto);

    default User userUpdateRequestDtoToUserWithIdAndAuditFields(
            @MappingTarget User user, UserUpdateRequestDto userUpdateRequestDto, long auditUserId) {
        if (userUpdateRequestDto.getUsername() != null && !userUpdateRequestDto.getUsername().isBlank())
            user.setUsername(userUpdateRequestDto.getUsername());
        if (userUpdateRequestDto.getFirstName() != null && !userUpdateRequestDto.getFirstName().isBlank())
            user.setFirstName(userUpdateRequestDto.getFirstName());
        if (userUpdateRequestDto.getLastName() != null && !userUpdateRequestDto.getLastName().isBlank())
            user.setLastName(userUpdateRequestDto.getLastName());
        if (userUpdateRequestDto.getEmailAddress() != null && !userUpdateRequestDto.getEmailAddress().isBlank())
            user.setEmailAddress(userUpdateRequestDto.getEmailAddress());
        user.setUpdatedBy(auditUserId);
        return  user;
    }

    @Mapping(target = "createdBy", source = "auditUserId")
    @Mapping(target = "updatedBy", source = "auditUserId")
    User mapUserRequestDtoToUserWithAuditFields(UserRequestDto userRequestDto, long auditUserId);
}
