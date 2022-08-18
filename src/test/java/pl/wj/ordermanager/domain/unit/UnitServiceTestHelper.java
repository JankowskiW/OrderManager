package pl.wj.ordermanager.domain.unit;

import pl.wj.ordermanager.domain.unit.model.Unit;

import java.util.ArrayList;
import java.util.List;

public class UnitServiceTestHelper {

    public static List<Unit> createExampleListOfUnits() {
        List<Unit> units = new ArrayList<>();
        units.add(new Unit(1, "kg", "kilogram"));
        units.add(new Unit(2, "g", "gram"));
        units.add(new Unit(3, "m", "meter"));
        units.add(new Unit(4, "mm", "milimeter"));
        units.add(new Unit(5, "m2", "square meter"));
        units.add(new Unit(6, "m3", "cubic meter"));
        units.add(new Unit(7, "pcs", "pieces"));
        units.add(new Unit(8, "pckg", "package"));
        return units;
    }
}
