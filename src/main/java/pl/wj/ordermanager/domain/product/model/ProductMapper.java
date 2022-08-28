package pl.wj.ordermanager.domain.product.model;

import org.mapstruct.Mapper;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto productRequestDtoToProductResponseDto(ProductRequestDto productRequestDto);
    ProductResponseDto productToProductResponseDto(Product product);
    Product productRequestDtoToProduct(ProductRequestDto productRequestDto);
    Product productResponseDtoToProduct(ProductResponseDto productResponseDto);
}
