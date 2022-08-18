package pl.wj.ordermanager.domain.product;

import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;

import java.util.ArrayList;
import java.util.List;

public class ProductServiceTestHelper {

    private static final int NUMBER_OF_PRODUCTS = 10;
    private static final List<ProductResponseDto> productResponseDtos = new ArrayList<>();

    static {
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();
        for (int i = 1; i <= NUMBER_OF_PRODUCTS; i++) {
            productResponseDtos.add(
                    ProductResponseDto.builder()
                        .id(i)
                        .name("Steel Sheet " + i * 50)
                        .SKU("ST" + i * 50)
                        .quantity(i*10.5)
                        .build());
        }
    }

    public static List<ProductResponseDto> getExampleListOfProductResponseDto() {
        return productResponseDtos;
    }
}
