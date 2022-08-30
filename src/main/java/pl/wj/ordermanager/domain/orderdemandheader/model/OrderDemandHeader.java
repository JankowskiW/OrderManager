package pl.wj.ordermanager.domain.orderdemandheader.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="order-demand-headers")
public class OrderDemandHeader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String description;
    private short sourceType;
    private boolean canceled;
    private String cancellationReason;
    private long canceledBy;
    private LocalDateTime canceledAt;
    private boolean confirmed;
    private long confirmedBy;
    private LocalDateTime confirmedAt;
    private long createdBy;
    private LocalDateTime createdAt;
    private long updatedBy;
    private LocalDateTime updatedAt;
    private boolean accepted;
    private long acceptedBy;
    private LocalDateTime acceptedAt;
}
