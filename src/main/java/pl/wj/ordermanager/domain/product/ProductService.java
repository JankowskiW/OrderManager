package pl.wj.ordermanager.domain.product;

import lombok.RequiredArgsConstructor;
import org.hibernate.StaleObjectStateException;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.dao.OptimisticLockingFailureException;
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
import pl.wj.ordermanager.exception.ResourceInvalidVersionException;
import pl.wj.ordermanager.exception.ResourceNotFoundException;
import pl.wj.ordermanager.user.UserRepository;

import javax.persistence.OptimisticLockException;

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
        return productMapper.productToProductResponseDto(productRepository.save(product));
    }

    private Product mapProductRequestDtoWithAuditFieldsToCreatedProduct(ProductRequestDto productRequestDto, long loggedInUser) {
        Product product = productMapper.productRequestDtoToProduct(productRequestDto);
        product.setCreatedBy(loggedInUser);
        product.setUpdatedBy(loggedInUser);
        return product;
    }

    private long getLoggedInUserId()  {
        return userRepository.getLoggedInUserId()
                .orElseThrow(() -> new UsernameNotFoundException("User not found in the database"));
    }

    public ProductResponseDto editProduct(long id, ProductRequestDto productRequestDto) {
    }

    @Transactional
    public ProductResponseDto updateProductQuantity(long id, ProductQtyDto productQtyDto)  {
        // TODO: 28.08.2022 Catch somehow OptimisticLockException
        Product product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("product"));
        product.setQuantity(productQtyDto.getQuantity());
        System.out.println(product.getVersion());
        product.setUpdatedBy(getLoggedInUserId());
        product = productRepository.save(product);
        return productMapper.productToProductResponseDto(product);
    }
}
