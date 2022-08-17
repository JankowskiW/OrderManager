package pl.wj.ordermanager.domain.unit;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitService {
    private final UnitRepository unitRepository;
}
