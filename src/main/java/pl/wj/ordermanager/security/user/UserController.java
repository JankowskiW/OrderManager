package pl.wj.ordermanager.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.wj.ordermanager.security.user.model.dto.UserCredentialsDto;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.security.user.model.dto.UserUpdateRequestDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/users")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public UserResponseDto addUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.addUser(userRequestDto);
    }

    @PatchMapping("/users/{id}")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public UserResponseDto editUser(
            @PathVariable long id, @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
            return userService.editUser(userUpdateRequestDto);
    }

}
