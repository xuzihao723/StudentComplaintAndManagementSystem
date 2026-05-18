package edu.demo.scfs.service;

import edu.demo.scfs.repository.SystemSettingRepository;
import edu.demo.scfs.domain.SystemSetting;
import edu.demo.scfs.web.dto.SettingsDtos.EmailSettingsResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SettingsServiceTest {
    private final SystemSettingRepository settings = mock(SystemSettingRepository.class);
    private final RecordingEmailApiClient emailApi = new RecordingEmailApiClient();

    @Test
    void usesEmailApiBeforeSmtpWhenApiKeyIsConfigured() {
        when(settings.findBySettingKey("email.enabled")).thenReturn(Optional.of(setting("email.enabled", "true")));

        SettingsService service = new SettingsService(
                settings,
                "smtp.gmail.com",
                587,
                "sender@gmail.com",
                "smtp-password",
                "resend",
                "re_test",
                "Complaint System <onboarding@resend.dev>",
                emailApi
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("student@example.edu");
        message.setSubject("Case updated");
        message.setText("Your complaint has a new update.");

        service.sendMail(message);

        assertThat(emailApi.to).isEqualTo("student@example.edu");
        assertThat(emailApi.subject).isEqualTo("Case updated");
        assertThat(emailApi.text).isEqualTo("Your complaint has a new update.");
        assertThat(emailApi.from).isEqualTo("Complaint System <onboarding@resend.dev>");
    }

    @Test
    void reportsApiProviderAsCompleteWhenEmailSendingIsEnabled() {
        when(settings.findBySettingKey("email.enabled")).thenReturn(Optional.of(setting("email.enabled", "true")));

        SettingsService service = new SettingsService(
                settings,
                "localhost",
                1025,
                "",
                "",
                "resend",
                "re_test",
                "Complaint System <onboarding@resend.dev>",
                emailApi
        );

        EmailSettingsResponse response = service.emailSettings();

        assertThat(response.enabled()).isTrue();
        assertThat(response.complete()).isTrue();
        assertThat(response.provider()).isEqualTo("Resend API");
        assertThat(response.apiConfigured()).isTrue();
    }

    private static final class RecordingEmailApiClient implements EmailApiClient {
        private String to;
        private String subject;
        private String text;
        private String from;

        @Override
        public void send(String to, String subject, String text, String from) {
            this.to = to;
            this.subject = subject;
            this.text = text;
            this.from = from;
        }
    }

    private SystemSetting setting(String key, String value) {
        SystemSetting setting = new SystemSetting();
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        return setting;
    }
}
