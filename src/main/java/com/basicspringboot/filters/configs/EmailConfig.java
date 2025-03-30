package com.basicspringboot.filters.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${spring.mail.host}")
    private String HOST;
    @Value("${spring.mail.port}")
    private Integer PORT;
    @Value("${spring.mail.username}")
    private String USERNAME;
    @Value("${spring.mail.password}")
    private String PASSWORD;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(HOST);
        mailSender.setPort(PORT);
        mailSender.setUsername(USERNAME);
        mailSender.setPassword(PASSWORD);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        /*props.put("mail.smtp.ssl.trust", HOST);
        props.put("mail.smtp.starttls.enable", "true");

        props.put("mail.smtp.writetimeout", "5000");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");*/

        return mailSender;
    }
}
