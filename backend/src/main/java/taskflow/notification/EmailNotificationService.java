package taskflow.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    private final JavaMailSender mailSender;
    private final String from;
    private final String to;

    public EmailNotificationService(JavaMailSender mailSender,
            @Value("${taskflow.notification.from}") String from,
            @Value("${taskflow.notification.to}") String to) {
        this.mailSender = mailSender;
        this.from = from;
        this.to = to;
    }

    @Override
    public void notifyTaskCreated(Long taskId, String title) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Nueva tarea: " + title);
        message.setText("Se creó la tarea #" + taskId + ": " + title);
        mailSender.send(message);
        log.info("Correo enviado para la tarea {}", taskId);
    }
}
