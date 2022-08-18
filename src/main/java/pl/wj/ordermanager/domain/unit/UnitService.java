package pl.wj.ordermanager.domain.unit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.domain.unit.model.Unit;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;

    public List<Unit> getUnits() {
        return unitRepository.findAll();
    }
}
