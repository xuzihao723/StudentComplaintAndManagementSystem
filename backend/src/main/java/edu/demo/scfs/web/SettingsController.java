package edu.demo.scfs.web;

import edu.demo.scfs.service.SettingsService;
import edu.demo.scfs.web.dto.SettingsDtos.EmailSettingsRequest;
import edu.demo.scfs.web.dto.SettingsDtos.EmailSettingsResponse;
import edu.demo.scfs.web.dto.SettingsDtos.TestEmailRequest;
import edu.demo.scfs.web.dto.SettingsDtos.TestEmailResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings/email")
public class SettingsController {
    private final SettingsService settings;

    public SettingsController(SettingsService settings) {
        this.settings = settings;
    }

    @GetMapping
    public EmailSettingsResponse email() {
        return settings.emailSettings();
    }

    @PutMapping
    public EmailSettingsResponse updateEmail(@RequestBody EmailSettingsRequest request) {
        return settings.updateEmailSettings(request);
    }

    @PostMapping("/test")
    public TestEmailResponse testEmail(@Valid @RequestBody TestEmailRequest request) {
        return settings.sendTestEmail(request.to());
    }
}
