package org.dev.systemaaa.service;


import lombok.RequiredArgsConstructor;
import org.dev.systemaaa.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(User user, String token) {
        String link = baseUrl + "/auth/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Подтвердите регистрацию");
        message.setText("Для подтверждения перейдите по ссылке:\n" + link);
        mailSender.send(message);
    }
}