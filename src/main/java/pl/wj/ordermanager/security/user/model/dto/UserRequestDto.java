package pl.wj.ordermanager.security.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
public class UserRequestDto {
    private String username;
    private String firstName;
    private String lastName;
    @Pattern(regexp="^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",message = "Email address is invalid")
    private String emailAddress;
    private String password;
}
