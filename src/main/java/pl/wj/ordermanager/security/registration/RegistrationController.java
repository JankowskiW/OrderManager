package pl.wj.ordermanager.security.registration;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.wj.ordermanager.security.user.model.dto.UserRequestDto;
import pl.wj.ordermanager.security.user.model.dto.UserResponseDto;

import javax.validation.Valid;

@RestController
@RequestMapping("api/registration")
@AllArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public UserResponseDto registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return registrationService.registerUser(userRequestDto);
    }

    @GetMapping("/confirm")
    public String confirmEmail(@RequestParam String token) {
        return registrationService.confirmEmail(token);
    }
}