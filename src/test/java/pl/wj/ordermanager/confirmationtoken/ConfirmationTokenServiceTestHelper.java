package pl.wj.ordermanager.confirmationtoken;

import pl.wj.ordermanager.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.user.UserServiceTestHelper;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmationTokenServiceTestHelper {

    static final long TOKEN_EXPIRATION_TIME = 15;
    private static LocalDateTime currentTimestamp = LocalDateTime.now();

    public static ConfirmationToken createExampleConfirmationToken() {
        ConfirmationToken confirmationToken = new ConfirmationToken(
                UUID.randomUUID().toString(),
                currentTimestamp,
                currentTimestamp.plusMinutes(TOKEN_EXPIRATION_TIME),
                UserServiceTestHelper.createExampleUser(false, 1L));
        return confirmationToken;
    }
}
