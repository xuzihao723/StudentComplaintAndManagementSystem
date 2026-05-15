package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.web.dto.AuthDtos.UserSummary;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserSummary toSummary(AppUser user) {
        Long departmentId = user.getDepartment() == null ? null : user.getDepartment().getId();
        String departmentName = user.getDepartment() == null ? null : user.getDepartment().getName();
        return new UserSummary(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getStatus(),
                departmentId,
                departmentName,
                user.isEmailVerified()
        );
    }
}
