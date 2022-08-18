package pl.wj.ordermanager.domain.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;

import java.awt.print.Pageable;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static pl.wj.ordermanager.domain.product.ProductServiceTestHelper.getExampleListOfProductResponseDto;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("Should return only one page of products")
    void shouldReturnJustOneButNotLastPageOfUnits() {
        // given
        int pageNumber = 0;
        int pageSize = 4;
        int firstElementIndex = pageNumber * pageSize;
        int lastElementIndex = (pageNumber + 1) * pageSize;
        List<ProductResponseDto> onePageOfProducts =
                getExampleListOfProductResponseDto().stream()
                        .skip(firstElementIndex)
                        .limit(pageSize)
                        .collect(Collectors.toList());
        given(productRepository.findProducts(any(Pageable.class))).willReturn(new PageImpl<>(onePageOfProducts));

        // when
        Page<ProductResponseDto> responseProducts =
                productService.getProducts(PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseProducts)
                .isNotNull()
                .hasSize(pageSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(onePageOfProducts);
    }
    
}