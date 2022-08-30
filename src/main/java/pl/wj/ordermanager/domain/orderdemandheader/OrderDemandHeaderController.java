package pl.wj.ordermanager.domain.orderdemandheader;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("order-demands/headers")
public class OrderDemandHeaderController {

    private final OrderDemandHeaderService orderDemandHeaderService;
}
