package pl.wj.ordermanager.domain.unit;

import pl.wj.ordermanager.domain.unit.model.Unit;
import pl.wj.ordermanager.domain.unit.model.dto.UnitRequestDto;

import java.util.ArrayList;
import java.util.List;

public class UnitServiceTestHelper {

    private static final List<Unit> units = new ArrayList<>();

    static {
        units.add(new Unit(1L, "kg", "kilogram"));
        units.add(new Unit(2L, "g", "gram"));
        units.add(new Unit(3L, "m", "meter"));
        units.add(new Unit(4L, "mm", "milimeter"));
        units.add(new Unit(5L, "m2", "square meter"));
        units.add(new Unit(6L, "m3", "cubic meter"));
        units.add(new Unit(7L, "pcs", "pieces"));
        units.add(new Unit(8L, "pckg", "package"));
    }

    public static List<Unit> getExampleListOfUnits() {
        return units;
    }

    public static Unit createExampleUnit(long id) {
        return new Unit(id, "l", "litre");
    }

    public static UnitRequestDto createExampleUnitRequestDto() {
        return UnitRequestDto.builder()
                .name("l")
                .description("litre")
                .build();
    }
}
