package pl.wj.ordermanager.domain.product.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pl.wj.ordermanager.domain.product.model.dto.ProductQtyDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponseDto productRequestDtoToProductResponseDto(ProductRequestDto productRequestDto);
    ProductResponseDto productToProductResponseDto(Product product);
    Product productResponseDtoToProduct(ProductResponseDto productResponseDto);

    @Mapping(target = "createdBy", source = "auditUserId")
    @Mapping(target = "updatedBy", source = "auditUserId")
    @Mapping(target = "quantity", source = "quantity")
    Product productRequestDtoToProductWithAuditFieldsAndQty(ProductRequestDto productRequestDto, long auditUserId, BigDecimal quantity);

    @Mapping(target = "updatedBy", source = "auditUserId")
    Product productRequestDtoToProductWithAuditFields(@MappingTarget Product product, ProductRequestDto productRequestDto, long auditUserId);

    @Mapping(target = "quantity", source = "productQtyDto.quantity")
    @Mapping(target = "updatedBy", source = "auditUserId")
    Product productQuantityDtoToProductWithAutodFields(@MappingTarget Product product, ProductQtyDto productQtyDto, long auditUserId);
}
