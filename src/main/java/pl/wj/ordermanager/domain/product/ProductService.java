package pl.wj.ordermanager.domain.product;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        return productRepository.getProducts(pageable);
    }

    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {
        throw new NotYetImplementedException();
    }
}
