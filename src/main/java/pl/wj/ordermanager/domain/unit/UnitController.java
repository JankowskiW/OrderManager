package pl.wj.ordermanager.domain.unit;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wj.ordermanager.domain.unit.model.Unit;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("units")
public class UnitController {
    private final UnitService unitService;

    @GetMapping
    public List<Unit> getUnits() {
        return unitService.getUnits();
    }
}
