package pl.wj.ordermanager.email;

public interface EmailSender {
    void send(String from, String to, String subject, String username, String confirmationLink, long tokenExpirationTime);
}
