package pl.wj.ordermanager.domain.unit;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("units")
public class UnitController {
    private final UnitService unitService;
}
