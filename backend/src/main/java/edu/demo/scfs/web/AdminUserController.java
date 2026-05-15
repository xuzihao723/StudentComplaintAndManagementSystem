package edu.demo.scfs.web;

import edu.demo.scfs.service.AdminUserService;
import edu.demo.scfs.web.dto.AdminDtos.StaffUserRequest;
import edu.demo.scfs.web.dto.AdminDtos.UpdateUserRequest;
import edu.demo.scfs.web.dto.AuthDtos.UserSummary;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {
    private final AdminUserService adminUsers;

    public AdminUserController(AdminUserService adminUsers) {
        this.adminUsers = adminUsers;
    }

    @GetMapping
    public List<UserSummary> listUsers() {
        return adminUsers.listUsers();
    }

    @PostMapping
    public UserSummary createStaffUser(@Valid @RequestBody StaffUserRequest request) {
        return adminUsers.createStaffUser(request);
    }

    @PutMapping("/{id}")
    public UserSummary updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return adminUsers.updateUser(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        adminUsers.deleteUser(id);
    }
}

