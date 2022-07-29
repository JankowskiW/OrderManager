package pl.wj.ordermanager.security.role;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/roles")
public class RoleController {
    private final RoleService roleService;
}
