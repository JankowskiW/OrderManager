package pl.wj.ordermanager.security.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCredentialsDto {
    private String username;
    private String password;
}
