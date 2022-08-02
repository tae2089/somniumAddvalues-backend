package bin.study.memo.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Data
@Configuration
@ToString
public class MailConfig {
    private static final String MAIL_DEBUG = "mail.debug";
    private static final String MAIL_SMTP_STARTTLS_REQUIRED = "mail.smtp.starttls.required";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";

    @Data
    @AllArgsConstructor
    public static class Smtp {
        private boolean auth;
        private boolean startTlsRequired;
        private boolean startTlsEnable;

    }

    @Value("${spring.mail.host}")
    private String host ;
    @Value("${spring.mail.protocol}")
    private String protocol;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.default-encoding}")
    private String defaultEncoding;
    private Smtp smtp = new Smtp(true,true,true);

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(getHost());
        mailSender.setProtocol(getProtocol());
        mailSender.setPort(getPort());
        mailSender.setUsername(getUsername());
        mailSender.setPassword(getPassword());
        mailSender.setDefaultEncoding(getDefaultEncoding());
        Properties properties = mailSender.getJavaMailProperties();
        properties.put(MAIL_SMTP_STARTTLS_REQUIRED, getSmtp().isStartTlsRequired());
        properties.put(MAIL_SMTP_STARTTLS_ENABLE, getSmtp().isStartTlsEnable());
        properties.put(MAIL_SMTP_AUTH, getSmtp().isAuth());
        properties.put(MAIL_DEBUG, true);
        mailSender.setJavaMailProperties(properties);
        return mailSender;
    }

//    @Bean
//    public TemplateResolver emailTemplateResolver() {
//        TemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
//        emailTemplateResolver.setPrefix("mails/");
//        emailTemplateResolver.setSuffix(".html");
//        emailTemplateResolver.setTemplateMode("HTML5");
//        emailTemplateResolver.setCharacterEncoding("UTF-8");
//        emailTemplateResolver.setCacheable(true);
//        return emailTemplateResolver;
//    }
}
