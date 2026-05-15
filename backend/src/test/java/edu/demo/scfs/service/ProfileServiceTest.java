package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.Role;
import edu.demo.scfs.repository.UserRepository;
import edu.demo.scfs.web.dto.ProfileDtos.ChangePasswordRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProfileServiceTest {
    private final UserRepository users = mock(UserRepository.class);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final ProfileService service = new ProfileService(users, encoder);

    @Test
    void changesPasswordOnlyWhenOldPasswordMatches() {
        AppUser user = user();
        user.setPasswordHash(encoder.encode("OldPass123!"));
        when(users.findById(1L)).thenReturn(Optional.of(user));

        service.changePassword(user, new ChangePasswordRequest("OldPass123!", "NewPass123!"));

        assertThat(encoder.matches("NewPass123!", user.getPasswordHash())).isTrue();
    }

    @Test
    void rejectsPasswordChangeWithWrongOldPassword() {
        AppUser user = user();
        user.setPasswordHash(encoder.encode("OldPass123!"));
        when(users.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> service.changePassword(user, new ChangePasswordRequest("bad-password", "NewPass123!")))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Old password");
    }

    private AppUser user() {
        AppUser user = new AppUser();
        user.setId(1L);
        user.setUsername("student1");
        user.setFullName("Demo Student");
        user.setEmail("student@example.edu");
        user.setRole(Role.STUDENT);
        return user;
    }
}
