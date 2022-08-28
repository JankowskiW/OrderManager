package pl.wj.ordermanager.domain.unit;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.domain.unit.model.UnitMapper;
import pl.wj.ordermanager.domain.unit.model.Unit;
import pl.wj.ordermanager.domain.unit.model.dto.UnitRequestDto;
import pl.wj.ordermanager.exception.ResourceExistsException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;

    private static UnitMapper unitMapper = Mappers.getMapper(UnitMapper.class);

    public List<Unit> getUnits() {
        return unitRepository.findAll();
    }

    public Unit addUnit(UnitRequestDto unitRequestDto) {
        if(unitRepository.existsByName(unitRequestDto.getName())) throw new ResourceExistsException("Unit", "name");
        Unit unit = unitMapper.unitRequestDtoToUnit(unitRequestDto);
        return unitRepository.save(unit);
    }
}
