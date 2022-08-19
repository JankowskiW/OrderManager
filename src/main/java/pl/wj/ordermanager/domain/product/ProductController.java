package pl.wj.ordermanager.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public Page<ProductResponseDto> getProducts(Pageable pageable) {
       return productService.getProducts(pageable);
    }

    @PostMapping
    public ProductResponseDto addProduct(@RequestBody ProductRequestDto productRequestDto) {
        return productService.addProduct(productRequestDto);
    }
}
