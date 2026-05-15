package edu.demo.scfs.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class SettingsDtos {
    private SettingsDtos() {
    }

    public record EmailSettingsResponse(
            boolean enabled,
            String host,
            int port,
            String username,
            boolean passwordConfigured,
            String fromAddress,
            boolean startTls,
            boolean complete
    ) {
    }

    public record EmailSettingsRequest(
            boolean enabled,
            String host,
            int port,
            String username,
            String password,
            String fromAddress,
            boolean startTls
    ) {
    }

    public record TestEmailRequest(@NotBlank @Email String to) {
    }

    public record TestEmailResponse(boolean sent, String message) {
    }
}
