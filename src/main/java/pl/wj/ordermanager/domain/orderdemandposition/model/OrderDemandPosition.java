package pl.wj.ordermanager.domain.orderdemandposition.model;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="order_demand_positions")
public class OrderDemandPosition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long headerId;
    private long productId;
    private BigDecimal orderedQuantity;
    private LocalDateTime deadline;
    private String remarks;
    private boolean rejected;
    private long rejectedBy;
    private LocalDateTime rejectedAt;
    private boolean accepted;
    private long acceptedBy;
    private LocalDateTime acceptedAt;
    private long createdBy;
    private LocalDateTime createdAt;
    private long updatedBy;
    private LocalDateTime updatedAt;
}
