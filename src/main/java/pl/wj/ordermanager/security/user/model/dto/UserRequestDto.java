package pl.wj.ordermanager.security.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRequestDto {
    private long id;
    private String username;
    private String emailAddress;
    private String password;
}
