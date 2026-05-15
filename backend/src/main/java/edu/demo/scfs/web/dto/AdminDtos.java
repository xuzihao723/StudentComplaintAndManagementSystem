package edu.demo.scfs.web.dto;

import edu.demo.scfs.domain.AccountStatus;
import edu.demo.scfs.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class AdminDtos {
    private AdminDtos() {
    }

    public record StaffUserRequest(
            @NotBlank @Size(min = 3, max = 40) String username,
            @NotBlank @Size(min = 8, max = 100) String password,
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email String email,
            @NotNull Role role,
            Long departmentId
    ) {
    }

    public record UpdateUserRequest(
            @NotBlank @Size(max = 120) String fullName,
            @NotBlank @Email String email,
            @NotNull AccountStatus status,
            Long departmentId
    ) {
    }
}

