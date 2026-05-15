package edu.demo.scfs.repository;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.Role;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);

    Optional<AppUser> findByEmail(String email);

    boolean existsByUsername(String username);

    List<AppUser> findByRole(Role role);
}
