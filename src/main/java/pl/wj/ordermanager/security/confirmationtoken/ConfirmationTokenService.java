package pl.wj.ordermanager.security.confirmationtoken;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.security.confirmationtoken.model.ConfirmationToken;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public void addConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }

    public Optional<ConfirmationToken> getConfirmationToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }

    public void updateConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }
}
