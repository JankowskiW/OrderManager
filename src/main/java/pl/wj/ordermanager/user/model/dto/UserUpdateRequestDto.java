package pl.wj.ordermanager.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Pattern;

@Getter
@Builder
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String firstName;
    private String lastName;
    private String username;
    @Pattern(regexp= "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",message = "Email address is invalid")
    private String emailAddress;
}