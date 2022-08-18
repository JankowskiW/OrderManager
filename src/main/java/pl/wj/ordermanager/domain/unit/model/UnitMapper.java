package pl.wj.ordermanager.domain.unit.model;

import org.mapstruct.Mapper;
import pl.wj.ordermanager.domain.unit.model.dto.UnitRequestDto;

@Mapper(componentModel = "spring")
public interface UnitMapper {
    Unit unitRequestDtoToUnit(UnitRequestDto unitRequestDto);
}
