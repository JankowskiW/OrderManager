package pl.wj.ordermanager.domain.unit.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UnitRequestDto {
    private String name;
    private String description;
}
