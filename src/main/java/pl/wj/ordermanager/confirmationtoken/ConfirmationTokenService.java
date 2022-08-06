package pl.wj.ordermanager.confirmationtoken;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wj.ordermanager.confirmationtoken.model.ConfirmationToken;

import java.util.Optional;

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

    public void updateConfirmationToken(ConfirmationToken confirmationToken) {
        confirmationTokenRepository.save(confirmationToken);
    }
}
