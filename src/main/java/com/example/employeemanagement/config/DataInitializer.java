package com.example.employeemanagement.config;

import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.JobTitle;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.entity.User;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.JobTitleRepository;
import com.example.employeemanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final JobTitleRepository jobTitleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           JobTitleRepository jobTitleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUserIfAbsent("admin", "admin@example.com", "Admin@123", Role.ROLE_ADMIN);
        createUserIfAbsent("hr", "hr@example.com", "Hr@12345", Role.ROLE_HR);

        if (departmentRepository.count() == 0) {
            departmentRepository.save(new Department("Human Resources", "HR", "Human resources department."));
            departmentRepository.save(new Department("Engineering", "ENG", "Engineering department."));
        }

        if (jobTitleRepository.count() == 0) {
            jobTitleRepository.save(new JobTitle("Software Engineer", "LEVEL_1", "Entry-level engineering role."));
            jobTitleRepository.save(new JobTitle("HR Specialist", "LEVEL_1", "Human resources specialist."));
        }
    }

    private void createUserIfAbsent(String username, String email, String password, Role role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }
}
