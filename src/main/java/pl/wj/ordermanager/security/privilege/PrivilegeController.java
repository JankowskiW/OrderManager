package pl.wj.ordermanager.security.privilege;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/privileges")
public class PrivilegeController {
    private final PrivilegeService privilegeService;

}
