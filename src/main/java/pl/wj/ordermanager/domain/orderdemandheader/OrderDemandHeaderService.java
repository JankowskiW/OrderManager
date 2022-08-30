package pl.wj.ordermanager.domain.orderdemandheader;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDemandHeaderService {

    private final OrderDemandHeaderRepository orderDemandHeaderRepository;
}
