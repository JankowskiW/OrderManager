package pl.wj.ordermanager.confirmationtoken;

import pl.wj.ordermanager.domain.confirmationtoken.model.ConfirmationToken;
import pl.wj.ordermanager.user.UserServiceTestHelper;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConfirmationTokenServiceTestHelper {

    static final long TOKEN_EXPIRATION_TIME = 15;
    private static LocalDateTime currentTimestamp = LocalDateTime.now();
    private final static long MINUTES_SINCE_RECEIVED_EMAIL = 2L;

    public static ConfirmationToken createExampleConfirmationToken(boolean confirmed,  boolean expired) {
        ConfirmationToken confirmationToken = new ConfirmationToken(
                generateExampleToken(),
                currentTimestamp,
                currentTimestamp.plusMinutes(TOKEN_EXPIRATION_TIME),
                UserServiceTestHelper.createExampleUser(false, 1L));
        if (expired) makeTokenExpired(confirmationToken);
        if (confirmed) makeTokenConfirmed(confirmationToken);
        return confirmationToken;
    }

    public static String generateExampleToken() {
        return UUID.randomUUID().toString();
    }

    private static void makeTokenExpired(ConfirmationToken token) {
        token.setCreatedAt(token.getCreatedAt().minusMinutes(TOKEN_EXPIRATION_TIME + 1));
        token.setExpiresAt(token.getExpiresAt().minusMinutes(TOKEN_EXPIRATION_TIME + 1));
    }

    private static void makeTokenConfirmed(ConfirmationToken token) {
        token.setConfirmedAt(token.getCreatedAt().plusMinutes(MINUTES_SINCE_RECEIVED_EMAIL));
    }

    public static ConfirmationToken createExampleConfirmationToken(String token) {
        return new ConfirmationToken(
                token,
                currentTimestamp,
                currentTimestamp.plusMinutes(TOKEN_EXPIRATION_TIME),
                UserServiceTestHelper.createExampleUser(false, 1L));
    }

}
