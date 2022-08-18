package pl.wj.ordermanager.domain.unit;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.wj.ordermanager.domain.unit.model.Unit;
import pl.wj.ordermanager.domain.unit.model.dto.UnitRequestDto;

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

    @PostMapping
    public Unit addUnit(@RequestBody UnitRequestDto unitRequestDto) {
        return unitService.addUnit(unitRequestDto);
    }
}
