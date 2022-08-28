package pl.wj.ordermanager.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.wj.ordermanager.domain.user.model.dto.UserPasswordDto;
import pl.wj.ordermanager.domain.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.domain.user.model.dto.UserUpdateRequestDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('USER_READ')")
    public Page<UserResponseDto> getUsers(
            @RequestParam(required = false) Boolean archived, Pageable pageable) {
        return userService.getUsers(archived, pageable);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserResponseDto editUser(
            @PathVariable long id, @Valid @RequestBody UserUpdateRequestDto userUpdateRequestDto) {
            return userService.editUser(id, userUpdateRequestDto);
    }

    @PutMapping("/password")
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public void changePassword(@RequestBody UserPasswordDto userPasswordDto) {
        userService.changePassword(userPasswordDto);
    }

    @PutMapping("/{emailAddress}/password")
    public void sendPasswordResetRequest(@PathVariable String emailAddress) {
        userService.sendPasswordResetRequest(emailAddress);
    }

    @GetMapping("/password")
    public String resetPassword(@RequestParam String token) {
        return userService.resetPassword(token);
    }

}
