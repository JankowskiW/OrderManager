package pl.wj.ordermanager.domain.orderdemandposition;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderDemandPositionService {

    private final OrderDemandPositionRepository orderDemandPositionRepository;
}
