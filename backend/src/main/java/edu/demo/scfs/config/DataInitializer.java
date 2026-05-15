package edu.demo.scfs.config;

import edu.demo.scfs.domain.AppUser;
import edu.demo.scfs.domain.Category;
import edu.demo.scfs.domain.Department;
import edu.demo.scfs.domain.Role;
import edu.demo.scfs.repository.CategoryRepository;
import edu.demo.scfs.repository.DepartmentRepository;
import edu.demo.scfs.repository.UserRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Bean
    @Order(1)
    CommandLineRunner seedData(
            DepartmentRepository departments,
            CategoryRepository categories,
            UserRepository users,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            Department academic = department(departments, "Academic Affairs", "Academic services and course administration.");
            Department facilities = department(departments, "Campus Facilities", "Classrooms, dormitory, cafeteria, and campus facilities.");
            Department studentAffairs = department(departments, "Student Affairs Office", "Central complaint review and assignment office.");

            category(categories, "Academic Services", "Course registration, grades, timetable, and academic support.", false, academic);
            category(categories, "Campus Facilities", "Classrooms, libraries, dormitory, and equipment issues.", true, facilities);
            category(categories, "Cafeteria Services", "Food quality, queues, pricing, and hygiene feedback.", true, facilities);
            category(categories, "Teaching Quality", "Teaching methods, classroom delivery, and learning experience.", false, academic);
            category(categories, "Administrative Support", "General administrative service concerns.", false, studentAffairs);

            user(users, passwordEncoder, "admin", "Admin123!", "System Administrator", "admin@example.edu", Role.ADMIN, null);
            user(users, passwordEncoder, "officer", "Officer123!", "Student Affairs Officer", "officer@example.edu", Role.OFFICER, studentAffairs);
            user(users, passwordEncoder, "facility_staff", "Staff123!", "Facility Staff", "facility@example.edu", Role.DEPARTMENT_STAFF, facilities);
            user(users, passwordEncoder, "academic_staff", "Staff123!", "Academic Staff", "academic@example.edu", Role.DEPARTMENT_STAFF, academic);
            user(users, passwordEncoder, "student1", "Student123!", "Demo Student", "student1@example.edu", Role.STUDENT, null);
        };
    }

    private Department department(DepartmentRepository departments, String name, String description) {
        return departments.findByName(name).orElseGet(() -> {
            Department item = new Department();
            item.setName(name);
            item.setDescription(description);
            return departments.save(item);
        });
    }

    private void category(
            CategoryRepository categories,
            String name,
            String description,
            boolean anonymousAllowed,
            Department defaultDepartment
    ) {
        categories.findByName(name).orElseGet(() -> {
            Category item = new Category();
            item.setName(name);
            item.setDescription(description);
            item.setAnonymousAllowed(anonymousAllowed);
            item.setDefaultDepartment(defaultDepartment);
            return categories.save(item);
        });
    }

    private void user(
            UserRepository users,
            PasswordEncoder passwordEncoder,
            String username,
            String password,
            String fullName,
            String email,
            Role role,
            Department department
    ) {
        if (users.existsByUsername(username)) {
            return;
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setFullName(fullName);
        user.setEmail(email);
        user.setRole(role);
        user.setDepartment(department);
        users.save(user);
    }
}
