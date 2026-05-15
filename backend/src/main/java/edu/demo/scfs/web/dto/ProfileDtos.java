package edu.demo.scfs.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ProfileDtos {
    private ProfileDtos() {
    }

    public record ProfileResponse(Long id, String username, String fullName, String email, String role, String departmentName) {
    }

    public record UpdateProfileRequest(@NotBlank String fullName, @NotBlank @Email String email) {
    }

    public record ChangePasswordRequest(
            @NotBlank String oldPassword,
            @NotBlank @Size(min = 8, max = 100) String newPassword
    ) {
    }
}
