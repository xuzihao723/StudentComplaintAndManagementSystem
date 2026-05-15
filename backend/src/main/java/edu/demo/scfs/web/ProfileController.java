package edu.demo.scfs.web;

import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.service.ProfileService;
import edu.demo.scfs.web.dto.ProfileDtos.ChangePasswordRequest;
import edu.demo.scfs.web.dto.ProfileDtos.ProfileResponse;
import edu.demo.scfs.web.dto.ProfileDtos.UpdateProfileRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    private final ProfileService profiles;

    public ProfileController(ProfileService profiles) {
        this.profiles = profiles;
    }

    @GetMapping
    public ProfileResponse profile(@AuthenticationPrincipal UserPrincipal principal) {
        return profiles.profile(principal.user());
    }

    @PutMapping
    public ProfileResponse update(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody UpdateProfileRequest request) {
        return profiles.updateProfile(principal.user(), request);
    }

    @PostMapping("/password")
    public void password(@AuthenticationPrincipal UserPrincipal principal, @Valid @RequestBody ChangePasswordRequest request) {
        profiles.changePassword(principal.user(), request);
    }
}
