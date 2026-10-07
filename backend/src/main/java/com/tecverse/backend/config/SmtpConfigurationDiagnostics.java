package com.tecverse.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SmtpConfigurationDiagnostics implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(SmtpConfigurationDiagnostics.class);

    private final Environment environment;
    private final String host;
    private final int port;
    private final String username;

    public SmtpConfigurationDiagnostics(
            Environment environment,
            @Value("${spring.mail.host:}") String host,
            @Value("${spring.mail.port:0}") int port,
            @Value("${spring.mail.username:}") String username) {

        this.environment = environment;
        this.host = host;
        this.port = port;
        this.username = username;
    }

    @Override
    public void run(ApplicationArguments args) {

        String password = environment.getProperty("spring.mail.password");
        if (!hasText(password)) {
            password = environment.getProperty("MAIL_PASSWORD");
        }
        boolean passwordPresent = hasText(password);

        if (!hasText(host) || port <= 0 || !hasText(username) || !passwordPresent) {
            log.warn("SMTP credentials are not fully configured. host={}, port={}, username={}, passwordPresent={}", host, port, username, passwordPresent);
            return;
        }

        log.info(
                "SMTP configuration detected for {}:{} using username {}. Mail password is present.",
                host,
                port,
                username
        );
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
