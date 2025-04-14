package com.onboarding.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    
    @Value("${app.notification.emails}")
    private String notificationEmails;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void notifyFailure(String hash, String errorMessage) {
        log.info("Enviando notificação de falha para hash: {}", hash);
        List<String> recipients = getRecipients();
        
        if (recipients.isEmpty()) {
            log.warn("Nenhum destinatário configurado para notificações. Não será enviado e-mail.");
            return;
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipients.toArray(new String[0]));
        message.setSubject("Falha no processamento de onboarding - Hash: " + hash);
        String body = "Ocorreu um erro ao processar o hash " + hash + ":\n\n" + errorMessage;
        body += "\n\nData/Hora: " + LocalDateTime.now().format(formatter);
        body += "\nUsuário: tenissonjr";
        
        message.setText(body);
        
        try {
            mailSender.send(message);
            log.info("Notificação de falha enviada com sucesso para hash: {}", hash);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação por e-mail: {}", e.getMessage(), e);
        }
    }
    
    private List<String> getRecipients() {
        if (!StringUtils.hasText(notificationEmails)) {
            return List.of();
        }
        return Arrays.asList(notificationEmails.split(","));
    }
}