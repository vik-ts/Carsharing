package com.carsharing.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailNotificationService {

    private JavaMailSender javaMailSender;

    @Autowired
    public MailNotificationService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    @Async
    public void sendPassword(String email, String password) throws MailException {

        log.info(email + " - Отправка пароля на почту...");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Пароль от сервиса Каршеринг");
        mail.setText("Ваш пароль: " + password);

        try {
            javaMailSender.send(mail);
            log.info(email + " - Пароль отправлен на почту");
        } catch (Exception e) {
            log.info(email + " - Не удалось отправить письмо: " + e.getMessage());
        }
    }

    @Async
    public void sendDecisionByCar(String email, Boolean Active, Boolean Reject, String comment) throws MailException {

        log.info(email + " - Отправка решения по объявлению на почту...");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Решение об активации объявления в сервисе Каршеринг");

        if (Active) {
            mail.setText("Ваше объявление об аренде авто АКТИВИРОВАНО.");
        } else if (Reject) {
            mail.setText("Ваше объявление об аренде авто ОТКЛОНЕНО.");
        }

        mail.setText("Комментарий от администратора: " + (comment.length() == 0? "<отсутствуют>" : comment));

        try {
            javaMailSender.send(mail);
            log.info(email + " - Решение по объявлению отправлено на почту");
        } catch (Exception e) {
            log.info(email + " - Не удалось отправить письмо: " + e.getMessage());
        }
    }
}