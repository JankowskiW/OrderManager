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
import pl.wj.ordermanager.domain.user.UserRepository;

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
        Product product = productMapper.productRequestDtoToProductWithAuditFieldsAndQty(productRequestDto, getLoggedInUserId(), new BigDecimal(0));
        return productMapper.productToProductResponseDto(productRepository.save(product));
    }


    public ProductResponseDto editProduct(long id, ProductRequestDto productRequestDto) {
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product"));
        if (productRepository.existsByNameOrSKU(productRequestDto.getName(), productRequestDto.getSKU())) {
            throw new ResourceExistsException("product", "name or sku");
        }
        product = productMapper.productRequestDtoToProductWithAuditFields(product, productRequestDto, getLoggedInUserId());
        product = productRepository.save(product);
        return productMapper.productToProductResponseDto(product);
    }

    @Transactional
    public ProductResponseDto updateProductQuantity(long id, ProductQtyDto productQtyDto)  {
        // TODO: 28.08.2022 Try somehow catch OptimisticLockException
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product"));
        product = productMapper.productQuantityDtoToProductWithAutodFields(product, productQtyDto, getLoggedInUserId());
        product = productRepository.save(product);
        return productMapper.productToProductResponseDto(product);
    }

    private long getLoggedInUserId()  {
        return userRepository.getLoggedInUserId()
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }
}
