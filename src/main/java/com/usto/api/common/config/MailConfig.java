package com.usto.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

//원래 자동으로 돼야하는데 오류나서 javaMailSender연결을 수동으로 만들었습니다
@Configuration
@Profile("dev") // application-dev.properties와 세트로 dev에서만 활성화 - 추후 릴리즈하면 릴리즈용 추가하면 됌
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.smtp.auth:true}")
    private boolean smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable:true}")
    private boolean starttlsEnable;

    @Value("${spring.mail.properties.mail.smtp.starttls.required:true}")
    private boolean starttlsRequired;

    @Value("${spring.mail.properties.mail.smtp.connectiontimeout:5000}")
    private int connectionTimeout;

    @Value("${spring.mail.properties.mail.smtp.timeout:5000}")
    private int timeout;

    @Value("${spring.mail.properties.mail.smtp.writetimeout:5000}")
    private int writeTimeout;

    /**
     * Spring이 가장 기본적으로 찾는 Bean 이름/타입: JavaMailSender (관례적으로 메서드명 javaMailSender)
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);
        sender.setJavaMailProperties(javaMailProperties());
        return sender;
    }

    private Properties javaMailProperties() {
        Properties p = new Properties();
        p.put("mail.transport.protocol", "smtp");

        // dev properties에 맞춘 값
        p.put("mail.smtp.auth", String.valueOf(smtpAuth));
        p.put("mail.smtp.starttls.enable", String.valueOf(starttlsEnable));
        p.put("mail.smtp.starttls.required", String.valueOf(starttlsRequired));

        // timeout
        p.put("mail.smtp.connectiontimeout", String.valueOf(connectionTimeout));
        p.put("mail.smtp.timeout", String.valueOf(timeout));
        p.put("mail.smtp.writetimeout", String.valueOf(writeTimeout));

        // 디버그는 dev에서만 true로 두고 싶으면 아래 유지
        p.put("mail.debug", "true");

        return p;
    }
}
