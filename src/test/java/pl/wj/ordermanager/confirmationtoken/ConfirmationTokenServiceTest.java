package pl.wj.ordermanager.confirmationtoken;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    @Test
    void shouldSaveNewConfirmationTokenIntoDatabase() {
    }

    @Test
    void shouldReturnConfirmationTokenDetailsByToken() {
    }

    @Test
    void shouldUpdateConfirmationTokenDetailsInDatabase() {
    }
}