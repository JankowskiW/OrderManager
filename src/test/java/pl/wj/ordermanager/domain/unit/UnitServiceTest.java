package pl.wj.ordermanager.domain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wj.ordermanager.domain.unit.model.Unit;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UnitServiceTest {

    @Mock
    private UnitRepository unitRepository;
    @InjectMocks
    private UnitService unitService;

    @Test
    @DisplayName("Should return list of units")
    void shouldReturnListOfUnits() {
        // given
        given(unitRepository.findAll()).willReturn(UnitServiceTestHelper.createExampleListOfUnits());

        // when
        List<Unit> responseUnits = unitService.getUnits();

        // then
        assertThat(responseUnits)
                .isNotNull()
                .hasSize(UnitServiceTestHelper.createExampleListOfUnits().size())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(UnitServiceTestHelper.createExampleListOfUnits());
    }

}