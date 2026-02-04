package com.konalyan.cleaning.notificationservice1.service;

import com.konalyan.cleaning.notificationservice1.dto.EmailNotification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "user-verification", groupId = "notification-service1")
    public void handleEmailNotification(EmailNotification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom("testerikpro@mail.ru"); // или можно через @Value из properties
            helper.setTo(notification.getEmail());
            helper.setSubject("Код подтверждения");
            helper.setText(
                    "Ваш код подтверждения: " + notification.getCode() + "\n\nКод действителен 3 минуты.",
                    false
            );

            mailSender.send(message);
            log.info("Verification code sent to {}", notification.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", notification.getEmail(), e.getMessage());
        }
    }
}
