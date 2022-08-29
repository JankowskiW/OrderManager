package pl.wj.ordermanager.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wj.ordermanager.domain.product.model.Product;
import pl.wj.ordermanager.domain.product.model.ProductMapper;
import pl.wj.ordermanager.domain.product.model.dto.ProductQtyDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;
import pl.wj.ordermanager.exception.ResourceExistsException;
import pl.wj.ordermanager.exception.ResourceNotFoundException;
import pl.wj.ordermanager.user.UserRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;

    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        return productRepository.getProducts(pageable);
    }

    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {
        if (productRepository.existsByNameOrSKU(productRequestDto.getName(), productRequestDto.getSKU())) {
            throw new ResourceExistsException("product", "name or SKU");
        }
        Product product = mapProductRequestDtoWithAuditFieldsToCreatedProduct(productRequestDto, getLoggedInUserId());
        product.setQuantity(new BigDecimal(0));
        return productMapper.productToProductResponseDto(productRepository.save(product));
    }

    private Product mapProductRequestDtoWithAuditFieldsToCreatedProduct(ProductRequestDto productRequestDto, long loggedInUser) {
        // TODO: 28.08.2022 Put that method somehow to ProductMapper in proper way
        Product product = productMapper.productRequestDtoToProduct(productRequestDto);
        product.setCreatedBy(loggedInUser);
        product.setUpdatedBy(loggedInUser);
        return product;
    }

    public ProductResponseDto editProduct(long id, ProductRequestDto productRequestDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product"));
        if (productRepository.existsByNameOrSKU(product.getName(), product.getSKU())) {
            throw new ResourceExistsException("product", "name or sku");
        }
        product = mapEditProductRequestDtoToProduct(product, productRequestDto, getLoggedInUserId());
        product = productRepository.save(product);
        return productMapper.productToProductResponseDto(product);
    }

    private Product mapEditProductRequestDtoToProduct(Product product, ProductRequestDto productRequestDto, long loggedInUser) {
        // TODO: 28.08.2022 Put that method somehow to ProductMapper in proper way
        product.setUpdatedBy(loggedInUser);
        product.setName(productRequestDto.getName());
        product.setSKU(productRequestDto.getSKU());
        product.setEAN(productRequestDto.getEAN());
        product.setDescription(productRequestDto.getDescription());
        return product;
    }

    @Transactional
    public ProductResponseDto updateProductQuantity(long id, ProductQtyDto productQtyDto)  {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product"));
        product.setQuantity(productQtyDto.getQuantity());
        product.setUpdatedBy(getLoggedInUserId());
        product = productRepository.save(product);
        return productMapper.productToProductResponseDto(product);
    }

    private long getLoggedInUserId()  {
        return userRepository.getLoggedInUserId()
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }
}
