package pl.wj.ordermanager.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserCredentialsDto {
    private String username;
    private String password;
}
