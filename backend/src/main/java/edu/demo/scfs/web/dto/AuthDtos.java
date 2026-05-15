package edu.demo.scfs.web.dto;

import edu.demo.scfs.domain.AccountStatus;
import edu.demo.scfs.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record RegisterStudentRequest(
            @NotBlank @Size(min = 3, max = 40) String username,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email String email
    ) {
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password
    ) {
    }

    public record AuthResponse(
            String token,
            UserSummary user
    ) {
    }

    public record UserSummary(
            Long id,
            String username,
            String fullName,
            String email,
            Role role,
            AccountStatus status,
            Long departmentId,
            String departmentName,
            boolean emailVerified
    ) {
    }

    public record TokenResponse(String token, String message) {
    }

    public record VerifyEmailRequest(@NotBlank String username, @NotBlank String token) {
    }

    public record ForgotPasswordRequest(@NotBlank String usernameOrEmail) {
    }

    public record ResetPasswordRequest(@NotBlank String username, @NotBlank String token, @NotBlank @Size(min = 8, max = 100) String newPassword) {
    }
}
