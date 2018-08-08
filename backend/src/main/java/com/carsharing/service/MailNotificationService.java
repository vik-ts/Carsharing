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
    public void sendDecisionByCar(String email, Boolean activated, Boolean rejected, String comment) throws MailException {

        log.info(email + " - Отправка решения по объявлению на почту...");

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(email);
        mail.setSubject("Решение об активации объявления в сервисе Каршеринг");

        if (activated) {
            mail.setText("Ваше объявление об аренде авто АКТИВИРОВАНО.");
        } else if (rejected) {
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

    @Async
    public void sendDecisionByCarBookingFromAdmin(String emailCarOwner, String emailCarBookingOwner, Boolean activated,
                                         Boolean rejected, String comment) throws MailException {

        SimpleMailMessage mail = new SimpleMailMessage();
        String email = "";

        mail.setSubject("Уведомление от сервиса Каршеринг");

        if (activated) {
            email = emailCarOwner;

            log.info(email + " - Бронь на аренду авто активирована администратором, отправка уведомления на почту арендодателя...");
            mail.setText("На ваше объявление об аренде авто создана бронь. Зайдите на сайт Каршеринг и подтвердите бронь.");
        } else if (rejected) {
            email = emailCarBookingOwner;

            log.info(email + " - Бронь на аренду авто отклонена администратором, отправка уведомления на почту арендатора...");
            mail.setText("Ваша бронь на аренду авто отклонена администратором.");
        }

        mail.setTo(email);
        mail.setText("Комментарий от администратора: " + (comment.length() == 0? "<отсутствуют>" : comment));

        try {
            javaMailSender.send(mail);
            log.info(email + " - Решение от администратора по брони на аренду авто отправлено на почту");
        } catch (Exception e) {
            log.info(email + " - Не удалось отправить письмо: " + e.getMessage());
        }
    }

    @Async
    public void sendDecisionByCarBookingFromCarOwner(String email, Boolean confirmed, Boolean rejected,
                                                     String comment) throws MailException {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setSubject("Уведомление от сервиса Каршеринг");

        if (confirmed) {
            log.info(email + " - Бронь на аренду авто одобрена арендодателем, отправка уведомления на почту арендатора...");

            mail.setText("Ваша бронь на аренду авто одобрена.");
        } else if (rejected) {
            log.info(email + " - Бронь на аренду авто отклонена арендодателем, отправка уведомления на почту арендатора...");

            mail.setText("Ваша бронь на аренду авто отклонена арендодателем.");
        }

        mail.setTo(email);
        mail.setText("Комментарий от арендодателя: " + (comment.length() == 0? "<отсутствуют>" : comment));

        try {
            javaMailSender.send(mail);
            log.info(email + " - Решение от арендодателя по брони на аренду авто отправлено на почту");
        } catch (Exception e) {
            log.info(email + " - Не удалось отправить письмо: " + e.getMessage());
        }
    }
}