package pl.wj.ordermanager.domain.orderdemandposition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wj.ordermanager.domain.orderdemandposition.model.OrderDemandPosition;

@Repository
public interface OrderDemandPositionRepository extends JpaRepository<OrderDemandPosition, Long> {
}
