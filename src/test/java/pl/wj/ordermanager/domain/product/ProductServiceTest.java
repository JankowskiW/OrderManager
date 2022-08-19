package pl.wj.ordermanager.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.wj.ordermanager.domain.product.model.Product;
import pl.wj.ordermanager.domain.product.model.ProductMapper;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;
import pl.wj.ordermanager.exception.ResourceExistsException;
import pl.wj.ordermanager.user.UserRepository;
import pl.wj.ordermanager.user.UserService;
import pl.wj.ordermanager.user.model.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static pl.wj.ordermanager.domain.product.ProductServiceTestHelper.*;
import static pl.wj.ordermanager.exception.ExceptionHelper.createResourceExistsExceptionMessage;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    private ProductService productService;

    @BeforeEach
    void setUpBeforeEach() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductService(
                productRepository,
                userRepository,
                Mappers.getMapper(ProductMapper.class)
        );
    }

    @Test
    @DisplayName("Should return only one page of products")
    void shouldReturnJustOneButNotLastPageOfUnits() {
        // given
        int pageNumber = 0;
        int pageSize = 4;
        int firstElementIndex = pageNumber * pageSize;
        List<ProductResponseDto> onePageOfProducts =
                getExampleListOfProductResponseDto().stream()
                        .skip(firstElementIndex)
                        .limit(pageSize)
                        .collect(Collectors.toList());
        given(productRepository.getProducts(any(Pageable.class))).willReturn(new PageImpl<>(onePageOfProducts));

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

    @Test
    @DisplayName("Should return just last page of products")
    void shouldReturnJustLastPageOfProducts() {
        // given
        int pageNumber = 2;
        int pageSize = 4;
        int firstElementIndex = pageNumber * pageSize;
        int expectedSize = getExampleListOfProductResponseDto().size() - pageNumber * pageSize;
        List<ProductResponseDto> lastPageOfProducts =
                getExampleListOfProductResponseDto().stream()
                        .skip(firstElementIndex)
                        .limit(pageSize)
                        .collect(Collectors.toList());
        given(productRepository.getProducts(any(Pageable.class))).willReturn(new PageImpl<>(lastPageOfProducts));

        // when
        Page<ProductResponseDto> responseProducts = productService.getProducts(PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(responseProducts)
                .isNotNull()
                .hasSize(expectedSize)
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(lastPageOfProducts);
    }

    @Test
    @DisplayName("Should add new product")
    void shouldAddNewProduct() {
        // given
        long id = 1L;
        long loggedInUserId = 1L;
        ProductRequestDto productRequestDto = createExampleProductRequestDto();
        ProductResponseDto expectedResponseDto = createExampleProductResponseDto(id, productRequestDto);
        given(productRepository.existsByNameOrSKU(anyString(), anyString())).willReturn(false);
        given(userRepository.getLoggedInUserId()).willReturn(Optional.of(loggedInUserId));
        given(productRepository.save(any(Product.class))).willAnswer(
                i -> {
                    Product p = i.getArgument(0, Product.class);
                    p.setId(id);
                    p.setCreatedAt(getCurrentTimestamp());
                    p.setUpdatedAt(getCurrentTimestamp());
                    return p;
                });

        // when
        ProductResponseDto responseProduct = productService.addProduct(productRequestDto);

        // then
        assertThat(responseProduct)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponseDto);
    }

    @Test
    @DisplayName("Should throw ResourceExistsException when product name or sku already exists in database")
    void shouldThrowExceptionWhenProductNameOrSkuAlreadyExists() {
        // given
        given(productRepository.existsByNameOrSKU(anyString(), anyString())).willReturn(true);

        // when
        assertThatThrownBy(() -> productService.addProduct(createExampleProductRequestDto()))
                .isInstanceOf(ResourceExistsException.class)
                .hasMessage(createResourceExistsExceptionMessage("product", "name or SKU"));
    }

}