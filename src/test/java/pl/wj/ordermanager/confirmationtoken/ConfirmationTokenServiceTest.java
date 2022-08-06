package pl.wj.ordermanager.confirmationtoken;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.wj.ordermanager.confirmationtoken.model.ConfirmationToken;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    @InjectMocks
    private ConfirmationTokenService confirmationTokenService;

    @Test
    @DisplayName("Should save new confirmation token in database")
    void shouldSaveNewConfirmationTokenInDatabase() {
        // given
        long tokenId = 1L;
        ConfirmationToken confirmationToken = ConfirmationTokenServiceTestHelper.createExampleConfirmationToken();
        ConfirmationToken expectedResponse = confirmationToken;
        confirmationToken.setId(tokenId);
        given(confirmationTokenRepository.save(any(ConfirmationToken.class))).willAnswer(
                i -> {
                    ConfirmationToken ct = i.getArgument(0, ConfirmationToken.class);
                    ct.setId(tokenId);
                    return ct;
                });

        // when
        confirmationToken = confirmationTokenService.addConfirmationToken(confirmationToken);

        // then
        assertThat(confirmationToken)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should find by token and return confirmation token details")
    void shouldFindByTokenAndReturnConfirmationTokenDetails() {
        // given
        long tokenId = 1L;
        ConfirmationToken expectedResponse = ConfirmationTokenServiceTestHelper.createExampleConfirmationToken();
        expectedResponse.setId(tokenId);
        given(confirmationTokenRepository.findByToken(anyString())).willReturn(Optional.of(expectedResponse));

        // when
        ConfirmationToken confirmationToken = confirmationTokenService.getConfirmationToken(expectedResponse.getToken());

        // then
        assertThat(confirmationToken)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when token does not exist in database")
    void shouldThrowExceptionWhenTokenDoesNotExist() {
        // given
        String token = "exampleToken";
        given(confirmationTokenRepository.findByToken(anyString())).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> confirmationTokenService.getConfirmationToken(token))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Token not found");
    }

    @Test
    @DisplayName("Should update confirmation token details in database")
    void shouldUpdateConfirmationTokenDetailsInDatabase() {
        // given
        long tokenId = 1L;
        ConfirmationToken confirmationToken = ConfirmationTokenServiceTestHelper.createExampleConfirmationToken();
        confirmationToken.setId(tokenId);
        ConfirmationToken expectedResponse = confirmationToken;
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        given(confirmationTokenRepository.save(any(ConfirmationToken.class))).willAnswer(
                i -> {
                    ConfirmationToken ct = i.getArgument(0, ConfirmationToken.class);
                    ct.setCreatedAt(expectedResponse.getConfirmedAt());
                    return ct;
                });

        // when
        confirmationToken = confirmationTokenService.addConfirmationToken(confirmationToken);

        // then
        assertThat(confirmationToken)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(expectedResponse);
    }
}