package pl.wj.ordermanager.security.email;

import org.springframework.mail.javamail.JavaMailSender;

public interface EmailSender {
    void send(String from, String to, String title, String content);
}
