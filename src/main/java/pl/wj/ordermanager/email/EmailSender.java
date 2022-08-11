package pl.wj.ordermanager.email;

public interface EmailSender {
    void sendRegistrationConfirmationToken(String from, String to, String subject, String username, String confirmationLink, long tokenExpirationTime);
    void sendPasswordResetConfirmationToken(String from, String to, String subject, String confirmationLink, long tokenExpirationTime);
}
