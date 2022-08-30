package pl.wj.ordermanager.domain.orderdemandheader;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wj.ordermanager.domain.orderdemandheader.model.OrderDemandHeader;

@Repository
public interface OrderDemandHeaderRepository extends JpaRepository<OrderDemandHeader, Long> {
}
