package pl.wj.ordermanager.domain.unit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wj.ordermanager.domain.unit.model.Unit;
import pl.wj.ordermanager.domain.unit.model.dto.UnitRequestDto;
import pl.wj.ordermanager.exception.ResourceExistsException;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static pl.wj.ordermanager.domain.unit.UnitServiceTestHelper.*;
import static pl.wj.ordermanager.exception.ExceptionHelper.createResourceExistsExceptionMessage;

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
        given(unitRepository.findAll()).willReturn(getExampleListOfUnits());

        // when
        List<Unit> response = unitService.getUnits();

        // then
        assertThat(response)
                .isNotNull()
                .hasSize(getExampleListOfUnits().size())
                .usingRecursiveFieldByFieldElementComparator()
                .isEqualTo(getExampleListOfUnits());
    }

    @Test
    @DisplayName("Should return empty list of units")
    void shouldReturnEmptyListOfUnits() {
        // given
        given(unitRepository.findAll()).willReturn(new ArrayList<>());

        // when
        List<Unit> response = unitService.getUnits();

        // then
        assertThat(response)
                .isNotNull()
                .hasSize(0);
    }

    @Test
    @DisplayName("Should add new unit if not exists")
    void shouldAddNewUnitIfNotExists() {
        // given
        long id = 9L;
        UnitRequestDto unitRequestDto = createExampleUnitRequestDto();
        Unit expectedResponse = createExampleUnit(id);
        given(unitRepository.existsByName(anyString())).willReturn(false);
        given(unitRepository.save(any(Unit.class))).
                willAnswer(
                        i -> {
                            Unit u = i.getArgument(0, Unit.class);
                            u.setId(id);
                            return u;
                        });

        // when
        Unit response = unitService.addUnit(unitRequestDto);

        // then
        assertThat(response)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw ResourceExistsException when unit already exists in database")
    void shouldThrowExceptionWhenUnitAlreadyExistsInDatabase() {
        // given
        given(unitRepository.existsByName(anyString())).willReturn(true);

        // when
        assertThatThrownBy(() -> unitService.addUnit(createExampleUnitRequestDto()))
                .isInstanceOf(ResourceExistsException.class)
                .hasMessage(createResourceExistsExceptionMessage("Unit","name"));

    }
}