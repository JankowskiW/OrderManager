package pl.wj.ordermanager.domain.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.wj.ordermanager.domain.product.model.Product;
import pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT new pl.wj.ordermanager.domain.product.model.dto.ProductResponseDto(" +
            "p.id, p.name, p.SKU, p.EAN , p.quantity, p.description) " +
            "FROM Product p")
    Page<ProductResponseDto> getProducts(Pageable pageable);

    boolean existsByNameOrSKU(String name, String sku);
}
