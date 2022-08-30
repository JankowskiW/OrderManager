package pl.wj.ordermanager.domain.orderdemandposition;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("order-demands/positions")
public class OrderDemandPositionController {

    private final OrderDemandPositionService orderDemandPositionService;
}
