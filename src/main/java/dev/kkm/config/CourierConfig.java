package dev.kkm.config;



import dev.kkm.service.CourierService;
import dev.kkm.service.CourierServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.thymeleaf.TemplateEngine;

import java.util.Properties;

@AutoConfiguration
@Configuration
@EnableConfigurationProperties({MailProperties.class})
public class CourierConfig {

    @Bean
    @ConditionalOnMissingBean
    public JavaMailSender mailSender(MailProperties properties) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(properties.getHost());
        mailSender.setPort(properties.getPort() != null?properties.getPort():25);
        mailSender.setUsername(properties.getUsername());
        mailSender.setPassword(properties.getPassword());
        mailSender.setDefaultEncoding(properties.getDefaultEncoding() != null? String.valueOf(properties.getDefaultEncoding()) : "UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.putAll(properties.getProperties());
        mailSender.setJavaMailProperties(props);

        return mailSender;
    }

    @Bean
    public CourierService courierService(ApplicationContext context, TemplateEngine engine) {
        return new CourierServiceImpl(context, engine);
    }
}
