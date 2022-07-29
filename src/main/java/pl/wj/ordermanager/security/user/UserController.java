package pl.wj.ordermanager.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;
import pl.wj.ordermanager.security.user.model.dto.UserUpdateRequestDto;

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
            return userService.editUser(userUpdateRequestDto);
    }

}
