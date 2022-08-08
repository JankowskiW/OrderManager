package pl.wj.ordermanager.confirmationtoken;

import pl.wj.ordermanager.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.user.UserServiceTestHelper;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmationTokenServiceTestHelper {

    static final long TOKEN_EXPIRATION_TIME = 15;
    private static LocalDateTime currentTimestamp = LocalDateTime.now();

    public static ConfirmationToken createExampleConfirmationToken() {
        return new ConfirmationToken(
                generateExampleToken(),
                currentTimestamp,
                currentTimestamp.plusMinutes(TOKEN_EXPIRATION_TIME),
                UserServiceTestHelper.createExampleUser(false, 1L));
    }

    public static ConfirmationToken createExampleConfirmationToken(String token) {
        return new ConfirmationToken(
                token,
                currentTimestamp,
                currentTimestamp.plusMinutes(TOKEN_EXPIRATION_TIME),
                UserServiceTestHelper.createExampleUser(false, 1L));
    }

    public static String generateExampleToken() {
        return UUID.randomUUID().toString();
    }
}
