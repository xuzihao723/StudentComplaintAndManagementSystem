package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.repository.UserRepository;
import edu.demo.scfs.web.dto.ProfileDtos.ChangePasswordRequest;
import edu.demo.scfs.web.dto.ProfileDtos.ProfileResponse;
import edu.demo.scfs.web.dto.ProfileDtos.UpdateProfileRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProfileService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public ProfileService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    public ProfileResponse profile(AppUser user) {
        return response(user);
    }

    @Transactional
    public ProfileResponse updateProfile(AppUser user, UpdateProfileRequest request) {
        AppUser managed = users.findById(user.getId()).orElseThrow();
        managed.setFullName(request.fullName().trim());
        managed.setEmail(request.email().trim());
        return response(managed);
    }

    @Transactional
    public void changePassword(AppUser user, ChangePasswordRequest request) {
        AppUser managed = users.findById(user.getId()).orElseThrow();
        if (!passwordEncoder.matches(request.oldPassword(), managed.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Old password is incorrect");
        }
        managed.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }

    private ProfileResponse response(AppUser user) {
        return new ProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name(),
                user.getDepartment() == null ? null : user.getDepartment().getName()
        );
    }
}
