package pl.wj.ordermanager.domain.confirmationtoken;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.domain.confirmationtoken.model.ConfirmationToken;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationToken addConfirmationToken(ConfirmationToken confirmationToken) {
       return confirmationTokenRepository.save(confirmationToken);
    }

    public ConfirmationToken getConfirmationToken(String token) throws RuntimeException {
        return confirmationTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Token not found"));
    }

    public ConfirmationToken updateConfirmationToken(ConfirmationToken confirmationToken) {
        return confirmationTokenRepository.save(confirmationToken);
    }
}
