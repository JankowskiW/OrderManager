package pl.wj.ordermanager.domain.privilege;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/privileges")
public class PrivilegeController {
    private final PrivilegeService privilegeService;

}
