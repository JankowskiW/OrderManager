package pl.wj.ordermanager.domain.unit;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.wj.ordermanager.domain.unit.model.Unit;

public interface UnitRepository extends JpaRepository<Unit, Long> {
    boolean existsByName(String name);
}
