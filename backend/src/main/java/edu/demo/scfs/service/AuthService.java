package edu.demo.scfs.service;

import edu.demo.scfs.domain.AccountStatus;
import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.Role;
import edu.demo.scfs.repository.UserRepository;
import edu.demo.scfs.security.JwtService;
import edu.demo.scfs.security.UserPrincipal;
import edu.demo.scfs.web.dto.AuthDtos.AuthResponse;
import edu.demo.scfs.web.dto.AuthDtos.ForgotPasswordRequest;
import edu.demo.scfs.web.dto.AuthDtos.LoginRequest;
import edu.demo.scfs.web.dto.AuthDtos.RegisterStudentRequest;
import edu.demo.scfs.web.dto.AuthDtos.ResetPasswordRequest;
import edu.demo.scfs.web.dto.AuthDtos.TokenResponse;
import edu.demo.scfs.web.dto.AuthDtos.UserSummary;
import edu.demo.scfs.web.dto.AuthDtos.VerifyEmailRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final TrackingCodeService trackingCodes;

    public AuthService(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserMapper userMapper,
            TrackingCodeService trackingCodes
    ) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.trackingCodes = trackingCodes;
    }

    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        if (users.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setRole(Role.STUDENT);
        user.setStatus(AccountStatus.ACTIVE);
        user.setEmailVerified(false);
        String token = trackingCodes.generatePlainCode();
        user.setEmailVerificationTokenHash(trackingCodes.hash(token));
        AppUser saved = users.save(user);
        return new AuthResponse(jwtService.createToken(saved), userMapper.toSummary(saved));
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
        AppUser user = users.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (user.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Account is disabled");
        }
        return new AuthResponse(jwtService.createToken(user), userMapper.toSummary(user));
    }

    public UserSummary me(UserPrincipal principal) {
        return userMapper.toSummary(principal.user());
    }

    @Transactional
    public TokenResponse verifyEmail(VerifyEmailRequest request) {
        AppUser user = users.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (!trackingCodes.matches(request.token(), user.getEmailVerificationTokenHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid verification token");
        }
        user.setEmailVerified(true);
        user.setEmailVerificationTokenHash(null);
        return new TokenResponse(null, "Email verified");
    }

    @Transactional
    public TokenResponse forgotPassword(ForgotPasswordRequest request) {
        AppUser user = users.findByUsername(request.usernameOrEmail())
                .or(() -> users.findByEmail(request.usernameOrEmail()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        String token = trackingCodes.generatePlainCode();
        user.setResetTokenHash(trackingCodes.hash(token));
        user.setResetTokenExpiresAt(Instant.now().plusSeconds(3600));
        return new TokenResponse(token, "Password reset token generated for demo SMTP flow");
    }

    @Transactional
    public TokenResponse resetPassword(ResetPasswordRequest request) {
        AppUser user = users.findByUsername(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        if (user.getResetTokenExpiresAt() == null || user.getResetTokenExpiresAt().isBefore(Instant.now())
                || !trackingCodes.matches(request.token(), user.getResetTokenHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired reset token");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setResetTokenHash(null);
        user.setResetTokenExpiresAt(null);
        return new TokenResponse(null, "Password reset successful");
    }
}
