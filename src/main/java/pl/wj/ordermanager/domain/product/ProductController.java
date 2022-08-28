package pl.wj.ordermanager.domain.product;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.wj.ordermanager.domain.product.model.dto.ProductQtyDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductRequestDto;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("products")
public class ProductController {
    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAuthority('PRODUCT_READ')")
    public Page<ProductResponseDto> getProducts(Pageable pageable) {
       return productService.getProducts(pageable);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'PRODUCT_WRITE')")
    public ProductResponseDto addProduct(@RequestBody ProductRequestDto productRequestDto) {
        return productService.addProduct(productRequestDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'PRODUCT_WRITE')")
    public ProductResponseDto editProduct(@PathVariable long id, @RequestBody ProductRequestDto productRequestDto) {
        return productService.editProduct(id, productRequestDto);
    }

    @PatchMapping("/{id}/quantity")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'PRODUCT_WRITE')")
    public ProductResponseDto updateProductQuantity(@PathVariable long id, @RequestBody ProductQtyDto productQtyDto) {
        return productService.updateProductQuantity(id, productQtyDto);
    }
}
