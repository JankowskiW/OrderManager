package pl.wj.ordermanager.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wj.ordermanager.security.user.model.User;
import pl.wj.ordermanager.security.user.model.dto.UserCredentialsDto;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public void login(@RequestBody UserCredentialsDto credentials) {
    }

    @PostMapping("/users")
    public User addUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.addUser(userRequestDto);
    }
}
