package pl.wj.ordermanager.domain.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.wj.ordermanager.domain.product.model.Product;
import pl.wj.ordermanager.domain.product.model.ProductMapper;
import pl.wj.ordermanager.domain.product.model.dto.ProductQtyDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;
import pl.wj.ordermanager.exception.ResourceExistsException;
import pl.wj.ordermanager.exception.ResourceNotFoundException;
import pl.wj.ordermanager.domain.user.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static pl.wj.ordermanager.domain.product.ProductServiceTestHelper.*;
import static pl.wj.ordermanager.exception.ExceptionHelper.createResourceExistsExceptionMessage;
import static pl.wj.ordermanager.exception.ExceptionHelper.createResourceNotFoundExceptionMessage;


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
        Page<ProductResponseDto> productResponseDtos =
                productService.getProducts(PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(productResponseDtos)
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
        Page<ProductResponseDto> productResponseDtos = productService.getProducts(PageRequest.of(pageNumber, pageSize));

        // then
        assertThat(productResponseDtos)
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
                    p.setVersion(1L);
                    return p;
                });

        // when
        ProductResponseDto productResponseDto = productService.addProduct(productRequestDto);

        // then
        assertThat(productResponseDto)
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

    @Test
    @DisplayName("Should edit product")
    void shouldEditProduct() {
        // given
        long id = 1L;
        long loggedInUserId = 1L;
        ProductRequestDto productRequestDto = createExampleProductRequestDto();
        productRequestDto.setDescription("New Description");
        ProductResponseDto expectedResponse = createExampleProductResponseDto(id, productRequestDto);
        expectedResponse.setQuantity(new BigDecimal(100));
        Product product = createExampleProduct(expectedResponse);
        product.setId(id);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(productRepository.existsByNameOrSKU(anyString(), anyString())).willReturn(false);
        given(userRepository.getLoggedInUserId()).willReturn(Optional.of(loggedInUserId));
        given(productRepository.save(any(Product.class))).willAnswer(
                i -> {
                    Product p = i.getArgument(0, Product.class);
                    p.setId(id);
                    p.setUpdatedAt(getCurrentTimestamp().plusDays(10));
                    p.setVersion(product.getVersion() + 1);
                    return p;
                });

        // when
        ProductResponseDto productResponseDto = productService.editProduct(id, productRequestDto);

        // then
        assertThat(productResponseDto)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when edited product does not exist in database by id")
    void shouldThrowExceptionWhenEditedProductDoesNotExistInDatabaseById() {
        // given
        long id = 1L;
        ProductRequestDto productRequestDto = createExampleProductRequestDto();
        given(productRepository.findById(anyLong())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> productService.editProduct(id, productRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(createResourceNotFoundExceptionMessage("product"));
    }

    @Test
    @DisplayName("Should throw ResourceExistsException when edited product exists by name or sku")
    void shouldThrowExceptionWhenEditedProductExistsByNameOrSKU() {
        // given
        long id = 1L;
        ProductRequestDto productRequestDto = createExampleProductRequestDto();
        ProductResponseDto expectedResponse = createExampleProductResponseDto(id, productRequestDto);
        Product product = createExampleProduct(expectedResponse);
        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(productRepository.existsByNameOrSKU(anyString(), anyString())).willReturn(true);

        // when
        assertThatThrownBy(() -> productService.editProduct(id, productRequestDto))
                .isInstanceOf(ResourceExistsException.class)
                .hasMessage(createResourceExistsExceptionMessage("product", "name or sku"));
    }

    @Test
    @DisplayName("Should update product quantity")
    void shouldUpdateProductQuantity() {
        // given
        long id = 1L;
        long loggedInUserId = 1L;
        ProductQtyDto productQtyDto = new ProductQtyDto(new BigDecimal(100));
        ProductResponseDto expectedResponse = getExampleProductResponseDto();
        Product product = createExampleProduct(expectedResponse);
        expectedResponse.setQuantity(productQtyDto.getQuantity());
        given(productRepository.findById(id)).willReturn(Optional.of(product));
        given(userRepository.getLoggedInUserId()).willReturn(Optional.of(loggedInUserId));
        given(productRepository.save(any(Product.class))).willAnswer(
                i -> {
                    Product p = i.getArgument(0, Product.class);
                    p.setUpdatedAt(getCurrentTimestamp().plusDays(10));
                    p.setVersion(product.getVersion() + 1);
                    return p;
                });

        // when
        ProductResponseDto productResponseDto = productService.updateProductQuantity(id, productQtyDto);

        // then
        assertThat(productResponseDto)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }


}