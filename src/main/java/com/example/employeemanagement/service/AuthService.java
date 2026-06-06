package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.auth.RegisterRequest;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.entity.User;
import com.example.employeemanagement.exception.BadRequestException;
import com.example.employeemanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    public void registerWebUser(RegisterRequest registerRequest) {
        registerRequest.setRole(null);
        User user = createUser(registerRequest);
        auditService.recordAction("User", user.getId(), "REGISTER", "Web registration for " + user.getUsername());
    }

    private User createUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken.");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRoles(resolveRegistrationRoles(registerRequest.getRole()));
        return userRepository.save(user);
    }

    private Set<Role> resolveRegistrationRoles(String role) {
        if (role == null || role.isBlank() || Role.ROLE_EMPLOYEE.name().equals(role)) {
            return Set.of(Role.ROLE_EMPLOYEE);
        }
        throw new BadRequestException("Invalid role specified.");
    }
}
