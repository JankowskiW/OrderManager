package pl.wj.ordermanager.security.role;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;
}
