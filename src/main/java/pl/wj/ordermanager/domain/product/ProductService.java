package pl.wj.ordermanager.domain.product;

import lombok.RequiredArgsConstructor;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;
import pl.wj.ordermanager.user.model.dto.UserResponseDto;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        throw new NotYetImplementedException();
    }
}
