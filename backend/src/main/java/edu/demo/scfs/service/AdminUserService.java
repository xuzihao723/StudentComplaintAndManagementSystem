package edu.demo.scfs.service;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.Department;
import edu.demo.scfs.domain.Role;
import edu.demo.scfs.repository.DepartmentRepository;
import edu.demo.scfs.repository.UserRepository;
import edu.demo.scfs.web.dto.AdminDtos.StaffUserRequest;
import edu.demo.scfs.web.dto.AdminDtos.UpdateUserRequest;
import edu.demo.scfs.web.dto.AuthDtos.UserSummary;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdminUserService {
    private final UserRepository users;
    private final DepartmentRepository departments;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public AdminUserService(
            UserRepository users,
            DepartmentRepository departments,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.users = users;
        this.departments = departments;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public List<UserSummary> listUsers() {
        return users.findAll().stream().map(userMapper::toSummary).toList();
    }

    @Transactional
    public UserSummary createStaffUser(StaffUserRequest request) {
        if (request.role() == Role.STUDENT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Students must use student registration");
        }
        if (users.existsByUsername(request.username())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }
        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setDepartment(resolveDepartment(request.departmentId(), request.role()));
        return userMapper.toSummary(users.save(user));
    }

    @Transactional
    public UserSummary updateUser(Long id, UpdateUserRequest request) {
        AppUser user = users.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setStatus(request.status());
        user.setDepartment(resolveDepartment(request.departmentId(), user.getRole()));
        return userMapper.toSummary(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!users.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        users.deleteById(id);
    }

    private Department resolveDepartment(Long departmentId, Role role) {
        if (role != Role.DEPARTMENT_STAFF) {
            return null;
        }
        if (departmentId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department staff require a department");
        }
        return departments.findById(departmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department not found"));
    }
}
