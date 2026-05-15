package edu.demo.scfs.service;

import edu.demo.scfs.domain.SystemSetting;
import edu.demo.scfs.repository.SystemSettingRepository;
import edu.demo.scfs.web.dto.SettingsDtos.EmailSettingsRequest;
import edu.demo.scfs.web.dto.SettingsDtos.EmailSettingsResponse;
import edu.demo.scfs.web.dto.SettingsDtos.TestEmailResponse;
import java.time.Instant;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SettingsService {
    private static final String PREFIX = "email.";
    private final SystemSettingRepository settings;
    private final String fallbackHost;
    private final int fallbackPort;
    private final String fallbackUsername;
    private final String fallbackPassword;

    public SettingsService(
            SystemSettingRepository settings,
            @Value("${spring.mail.host:localhost}") String fallbackHost,
            @Value("${spring.mail.port:1025}") int fallbackPort,
            @Value("${spring.mail.username:}") String fallbackUsername,
            @Value("${spring.mail.password:}") String fallbackPassword
    ) {
        this.settings = settings;
        this.fallbackHost = fallbackHost;
        this.fallbackPort = fallbackPort;
        this.fallbackUsername = fallbackUsername;
        this.fallbackPassword = fallbackPassword;
    }

    @Transactional(readOnly = true)
    public EmailSettingsResponse emailSettings() {
        boolean enabled = bool("enabled", false);
        String host = value("host", fallbackHost);
        int port = integer("port", fallbackPort);
        String username = value("username", fallbackUsername);
        String password = value("password", fallbackPassword);
        String from = value("fromAddress", username);
        boolean tls = bool("startTls", false);
        boolean complete = enabled && host != null && !host.isBlank() && port > 0 && from != null && !from.isBlank();
        return new EmailSettingsResponse(enabled, host, port, username, password != null && !password.isBlank(), from, tls, complete);
    }

    @Transactional
    public EmailSettingsResponse updateEmailSettings(EmailSettingsRequest request) {
        put("enabled", String.valueOf(request.enabled()));
        put("host", request.host());
        put("port", String.valueOf(request.port() <= 0 ? 1025 : request.port()));
        put("username", request.username());
        if (request.password() != null && !request.password().isBlank()) {
            put("password", request.password());
        }
        put("fromAddress", request.fromAddress());
        put("startTls", String.valueOf(request.startTls()));
        return emailSettings();
    }

    public void sendMail(SimpleMailMessage message) {
        EmailSettingsResponse config = emailSettings();
        if (!config.enabled() || !config.complete()) {
            throw new IllegalStateException("SMTP email is not enabled or incomplete");
        }
        JavaMailSenderImpl sender = sender(config);
        message.setFrom(config.fromAddress());
        sender.send(message);
    }

    public TestEmailResponse sendTestEmail(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("SCFS email test");
            message.setText("This is a test email from the Student Complaint and Feedback System.");
            sendMail(message);
            return new TestEmailResponse(true, "Test email sent");
        } catch (RuntimeException ex) {
            return new TestEmailResponse(false, ex.getMessage() == null ? "Test email failed" : ex.getMessage());
        }
    }

    private JavaMailSenderImpl sender(EmailSettingsResponse config) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.host());
        sender.setPort(config.port());
        sender.setUsername(config.username());
        sender.setPassword(value("password", fallbackPassword));
        Properties properties = sender.getJavaMailProperties();
        properties.put("mail.smtp.auth", String.valueOf(config.username() != null && !config.username().isBlank()));
        properties.put("mail.smtp.starttls.enable", String.valueOf(config.startTls()));
        return sender;
    }

    private String value(String key, String fallback) {
        return settings.findBySettingKey(PREFIX + key).map(SystemSetting::getSettingValue).orElse(fallback);
    }

    private boolean bool(String key, boolean fallback) {
        return Boolean.parseBoolean(value(key, String.valueOf(fallback)));
    }

    private int integer(String key, int fallback) {
        try {
            return Integer.parseInt(value(key, String.valueOf(fallback)));
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private void put(String key, String value) {
        SystemSetting item = settings.findBySettingKey(PREFIX + key).orElseGet(SystemSetting::new);
        item.setSettingKey(PREFIX + key);
        item.setSettingValue(value == null ? "" : value.trim());
        item.setUpdatedAt(Instant.now());
        settings.save(item);
    }
}
