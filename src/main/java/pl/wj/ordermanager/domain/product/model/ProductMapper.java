package pl.wj.ordermanager.domain.product.model;

import org.mapstruct.Mapper;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto productRequestDtoToProductResponseDto(ProductRequestDto productRequestDto);
    Product productRequestDtoToProduct(ProductRequestDto productRequestDto);
    ProductResponseDto productToProductResponseDto(Product product);

}
