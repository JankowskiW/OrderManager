package pl.wj.ordermanager.email;

import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendRegistrationConfirmationToken(String from, String to, String subject, String username, String confirmationLink, long tokenExpirationTime) {
        String content = prepareRegistrationConfirmationEmailContent(username, confirmationLink, tokenExpirationTime);
        send(from, to, subject, content);
    }
    private String prepareRegistrationConfirmationEmailContent(String username, String confirmationLink, long tokenExpirationTime) {
        Context context = new Context();
        context.setVariable("username", username);
        context.setVariable("confirmationlink", confirmationLink);
        context.setVariable("exptime", tokenExpirationTime);
        return templateEngine.process("registerconfirmemailtemplate", context);
    }

    @Override
    public void sendPasswordResetConfirmationToken(String from, String to, String subject, String confirmationLink, long tokenExpirationTime) {
        String content = preparePasswordResetConfirmationEmailContent(confirmationLink, tokenExpirationTime);
        send(from,to, subject, content);
    }

    private String preparePasswordResetConfirmationEmailContent(String confirmationLink, long tokenExpirationTime) {
        Context context = new Context();
        context.setVariable("confirmationlink", confirmationLink);
        context.setVariable("exptime", tokenExpirationTime);
        return templateEngine.process("passwordresetconfirmemailtemplate", context);
    }

    @Async
    void send(String from, String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = prepareMimeMessage(content, to, subject, from);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email");
        }
    }

    private MimeMessage prepareMimeMessage(String content, String to, String subject, String from) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
        messageHelper.setText(content, true);
        messageHelper.setTo(to);
        messageHelper.setSubject(subject);
        messageHelper.setFrom(from);
        return mimeMessage;
    }
}
