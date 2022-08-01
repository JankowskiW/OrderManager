package pl.wj.ordermanager.security.email;

public interface EmailSender {
    void send(String from, String to, String subject, String username, String confirmationLink, long tokenExpirationTime);
}
