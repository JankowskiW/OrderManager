package pl.wj.ordermanager.domain.product.model;

import lombok.*;
import pl.wj.ordermanager.domain.unit.model.Unit;
import pl.wj.ordermanager.role.model.Role;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String SKU;
    private String EAN;
    private BigDecimal quantity;
    private String description;
    private long createdBy;
    private LocalDateTime createdAt;
    private long updatedBy;
    private LocalDateTime updatedAt;
    private Long archivedBy;
    private LocalDateTime archivedAt;
    @Version
    private long version;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_units",
            joinColumns = {@JoinColumn(name="product_id")},
            inverseJoinColumns = {@JoinColumn(name="unit_id")}
    )
    private List<Unit> units;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    @PreUpdate
    private void onUpdate() { updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);}

}
