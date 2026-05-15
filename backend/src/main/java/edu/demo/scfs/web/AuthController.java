package edu.demo.scfs.web;

import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.service.AuthService;
import edu.demo.scfs.web.dto.AuthDtos.AuthResponse;
import edu.demo.scfs.web.dto.AuthDtos.ForgotPasswordRequest;
import edu.demo.scfs.web.dto.AuthDtos.LoginRequest;
import edu.demo.scfs.web.dto.AuthDtos.RegisterStudentRequest;
import edu.demo.scfs.web.dto.AuthDtos.ResetPasswordRequest;
import edu.demo.scfs.web.dto.AuthDtos.TokenResponse;
import edu.demo.scfs.web.dto.AuthDtos.UserSummary;
import edu.demo.scfs.web.dto.AuthDtos.VerifyEmailRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register/student")
    public AuthResponse registerStudent(@Valid @RequestBody RegisterStudentRequest request) {
        return auth.registerStudent(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return auth.login(request);
    }

    @GetMapping("/me")
    public UserSummary me(@AuthenticationPrincipal UserPrincipal principal) {
        return auth.me(principal);
    }

    @PostMapping("/verify-email")
    public TokenResponse verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        return auth.verifyEmail(request);
    }

    @PostMapping("/forgot-password")
    public TokenResponse forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return auth.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public TokenResponse resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return auth.resetPassword(request);
    }
}
