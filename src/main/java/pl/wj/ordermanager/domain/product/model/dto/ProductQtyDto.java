package pl.wj.ordermanager.domain.product.model.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductQtyDto {
    BigDecimal quantity;
}
