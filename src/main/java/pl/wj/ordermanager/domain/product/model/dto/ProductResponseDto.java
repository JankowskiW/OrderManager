package pl.wj.ordermanager.domain.product.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductResponseDto {
    private long id;
    private String name;
    private String SKU;
    private String EAN;
    private double quantity;
    private String description;
}
