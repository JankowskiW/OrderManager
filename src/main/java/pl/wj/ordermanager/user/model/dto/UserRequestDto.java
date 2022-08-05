package pl.wj.ordermanager.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserRequestDto {
    private String username;
    private String firstName;
    private String lastName;
    @Pattern(regexp="^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",message = "Email address is invalid")
    private String emailAddress;
    private String password;
}
