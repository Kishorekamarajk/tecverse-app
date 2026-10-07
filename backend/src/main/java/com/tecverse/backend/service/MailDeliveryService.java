package com.tecverse.backend.service;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailDeliveryService {

    private static final Logger log =
            LoggerFactory.getLogger(MailDeliveryService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final boolean consoleFallbackEnabled;
    private final String smtpHost;
    private final int smtpPort;

    public MailDeliveryService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username:noreply-cbt@cdac.in}") String fromAddress,
            @Value("${tecverse.mail.console-fallback:true}") boolean consoleFallbackEnabled,
            @Value("${spring.mail.host:smtp.cdac.in}") String smtpHost,
            @Value("${spring.mail.port:587}") int smtpPort) {

        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.consoleFallbackEnabled = consoleFallbackEnabled;
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
    }

    /**
     * Sends an email using the default label.
     */
    public boolean sendSimple(
            String to,
            String subject,
            String text) {

        return sendSimple(to, subject, text, "Email");
    }

    /**
     * Sends an email with a custom fallback/log label.
     */
    public boolean sendSimple(
            String to,
            String subject,
            String text,
            String fallbackLabel) {

        SimpleMailMessage message = new SimpleMailMessage();

        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            log.info(
                    "Attempting SMTP delivery via {}:{} for {} email to {}.",
                    smtpHost,
                    smtpPort,
                    fallbackLabel,
                    to
            );

            mailSender.send(message);

            log.info(
                    "SMTP delivery via {}:{} succeeded for {} email to {}.",
                    smtpHost,
                    smtpPort,
                    fallbackLabel,
                    to
            );

            return true;

        } catch (MailException ex) {

            String reason = rootMessage(ex);

            logMailFailure(fallbackLabel, reason, ex);

            if (consoleFallbackEnabled) {

                log.warn(
                        "{} email could not be delivered via SMTP to {}. Console fallback is active.",
                        fallbackLabel,
                        to
                );

                log.warn(
                        "\n================= {} FALLBACK EMAIL =================\nTO: {}\nSUBJECT: {}\nBODY:\n{}\n======================================================",
                        fallbackLabel,
                        to,
                        subject,
                        text
                );

                return true;
            }

            throw new IllegalStateException(
                    "Unable to send email right now. Please try again later.",
                    ex
            );
        } catch (Exception ex) {
            log.error("Unexpected error sending email to {}: {}", to, ex.getMessage(), ex);
            if (consoleFallbackEnabled) {
                log.warn(
                        "\n================= {} FALLBACK EMAIL =================\nTO: {}\nSUBJECT: {}\nBODY:\n{}\n======================================================",
                        fallbackLabel,
                        to,
                        subject,
                        text
                );
                return true;
            }
            throw new IllegalStateException("Email delivery failed: " + ex.getMessage(), ex);
        }
    }

    public boolean isConsoleFallbackEnabled() {
        return consoleFallbackEnabled;
    }

    public static String rootMessage(Throwable throwable) {

        Throwable root = throwable;

        while (root.getCause() != null) {
            root = root.getCause();
        }

        return root.getMessage() == null
                ? throwable.getMessage()
                : root.getMessage();
    }

    private void logMailFailure(
            String label,
            String reason,
            MailException exception) {

        if (hasCause(exception, MailAuthenticationException.class)
                || containsIgnoreCase(reason, "authentication")) {

            log.error(
                    "SMTP authentication failed via {}:{} while sending {} email. Verify the account credentials and SMTP access. ({})",
                    smtpHost,
                    smtpPort,
                    label,
                    reason
            );

        } else if (hasCause(exception, SocketTimeoutException.class)
                || containsIgnoreCase(reason, "timed out")) {

            log.error(
                    "SMTP connection timed out via {}:{} while sending {} email.",
                    smtpHost,
                    smtpPort,
                    label
            );

        } else if (hasCause(exception, ConnectException.class)
                || containsIgnoreCase(reason, "connection refused")) {

            log.error(
                    "SMTP connection was refused by {}:{} while sending {} email.",
                    smtpHost,
                    smtpPort,
                    label
            );

        } else if (containsIgnoreCase(reason, "starttls")
                || containsIgnoreCase(reason, "tls")) {

            log.error(
                    "SMTP STARTTLS negotiation failed via {}:{} while sending {} email. ({})",
                    smtpHost,
                    smtpPort,
                    label,
                    reason
            );

        } else {

            log.error(
                    "SMTP delivery failed via {}:{} while sending {} email: {}",
                    smtpHost,
                    smtpPort,
                    label,
                    reason
            );
        }
    }

    private static boolean hasCause(
            Throwable throwable,
            Class<? extends Throwable> type) {

        for (
                Throwable current = throwable;
                current != null;
                current = current.getCause()
        ) {
            if (type.isInstance(current)) {
                return true;
            }
        }

        return false;
    }

    private static boolean containsIgnoreCase(
            String value,
            String fragment) {

        return value != null
                && value.toLowerCase(Locale.ROOT).contains(fragment);
    }
}
