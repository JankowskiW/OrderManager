package pl.wj.ordermanager.domain.product;

import org.mapstruct.factory.Mappers;
import pl.wj.ordermanager.domain.product.model.Product;
import pl.wj.ordermanager.domain.product.model.ProductMapper;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProductServiceTestHelper {

    private static final int NUMBER_OF_PRODUCTS = 10;
    private static final List<ProductResponseDto> productResponseDtos = new ArrayList<>();
    private static LocalDateTime currentTimestamp = LocalDateTime.now();
    private static ProductMapper productMapper = Mappers.getMapper(ProductMapper.class);

    static {
        for (int i = 1; i <= NUMBER_OF_PRODUCTS; i++) {
            productResponseDtos.add(
                    ProductResponseDto.builder()
                        .id(i)
                        .name("Steel Sheet " + i * 50)
                        .SKU("ST" + i * 50)
                        .quantity(new BigDecimal(i*10))
                        .build());
        }
    }

    static ProductResponseDto getExampleProductResponseDto() {
        return productResponseDtos.get(1);
    }

    static List<ProductResponseDto> getExampleListOfProductResponseDto() {
        return productResponseDtos;
    }

    static ProductResponseDto createExampleProductResponseDto(long id, ProductRequestDto productRequestDto) {
        ProductResponseDto productResponseDto = productMapper.productRequestDtoToProductResponseDto(productRequestDto);
        productResponseDto.setId(id);
        return productResponseDto;
    }

    static ProductRequestDto createExampleProductRequestDto() {
        return ProductRequestDto.builder()
                .name("New product name")
                .SKU("NPN")
                .quantity(new BigDecimal(100.1))
                .description("Description of new product")
                .build();
    }

    static Product createExampleProduct(long id) {
        Product product = new Product();
        product.setId(id);
        product.setName("Product name");
        product.setSKU("Product SKU");
        product.setQuantity(new BigDecimal(0));
        product.setCreatedBy(1L);
        product.setCreatedAt(currentTimestamp);
        product.setUpdatedBy(1L);
        product.setCreatedAt(currentTimestamp);
        product.setVersion(1L);
        return product;
    }

    static LocalDateTime getCurrentTimestamp() {
        return currentTimestamp;
    }
}
