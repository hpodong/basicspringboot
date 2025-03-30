package com.basicspringboot.filters.services.api;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String FROM_EMAIL;

    @Value("${email.sender}")
    private String EMAIL_SENDER;

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    public void send(String to, String subject, String content) throws MailSendException, MessagingException, UnsupportedEncodingException {
        /*final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(FROM_EMAIL);

        mailSender.send(message);*/

        final MimeMessage message = mailSender.createMimeMessage();
        final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // 발신자 정보 설정
        helper.setFrom(FROM_EMAIL, EMAIL_SENDER); // 발신자명과 이메일 설정
        helper.setTo(to); // 수신자 이메일
        helper.setSubject(subject); // 이메일 제목
        helper.setText(content, true); // 이메일 내용 (HTML 가능)

        // 이메일 전송
        mailSender.send(message);
    }

    public String setContext(String code, String type) {
        Context context = new Context();
        context.setVariable("code", code);
        return templateEngine.process(type, context);
    }
}
