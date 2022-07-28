package pl.wj.ordermanager.security.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserUpdateRequestDto {
    private String username;
    private String emailAddress;
}
