package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.employee.EmployeeDto;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.JobTitle;
import com.example.employeemanagement.entity.User;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.JobTitleRepository;
import com.example.employeemanagement.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final JobTitleRepository jobTitleRepository;
    private final AuditService auditService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           JobTitleRepository jobTitleRepository,
                           AuditService auditService) {
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.jobTitleRepository = jobTitleRepository;
        this.auditService = auditService;
    }

    public long count() {
        return employeeRepository.count();
    }

    public List<EmployeeDto> findAll() {
        return employeeRepository.findAllWithDetails().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public EmployeeDto findById(Long id) {
        return mapToDto(getEmployee(id));
    }

    public EmployeeDto createEmployee(EmployeeDto dto) {
        User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found."));
        Employee employee = new Employee();
        employee.setUser(user);
        applyDto(employee, dto);
        Employee saved = employeeRepository.save(employee);
        auditService.recordAction("Employee", saved.getId(), "CREATE",
            saved.getFirstName() + " " + saved.getLastName() + " linked to " + user.getUsername());
        return mapToDto(saved);
    }

    public EmployeeDto updateEmployee(Long id, EmployeeDto dto) {
        Employee employee = getEmployee(id);
        applyDto(employee, dto);
        Employee saved = employeeRepository.save(employee);
        auditService.recordAction("Employee", saved.getId(), "UPDATE",
            "Updated profile for " + saved.getFirstName() + " " + saved.getLastName());
        return mapToDto(saved);
    }

    public void deleteEmployee(Long id) {
        Employee employee = getEmployee(id);
        auditService.recordAction("Employee", employee.getId(), "DELETE",
            "Removed " + employee.getFirstName() + " " + employee.getLastName());
        employeeRepository.delete(employee);
    }

    public EmployeeDto findByUsername(String username) {
        Employee employee = employeeRepository.findByUserUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("Employee record not found for user."));
        return mapToDto(employee);
    }

    private void applyDto(Employee employee, EmployeeDto dto) {
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setHireDate(dto.getHireDate());
        employee.setContactNumber(dto.getContactNumber());
        employee.setAddress(dto.getAddress());
        employee.setStatus(dto.getStatus() == null ? "ACTIVE" : dto.getStatus());
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found."));
            employee.setDepartment(department);
        } else {
            employee.setDepartment(null);
        }
        if (dto.getJobTitleId() != null) {
            JobTitle jobTitle = jobTitleRepository.findById(dto.getJobTitleId())
                .orElseThrow(() -> new ResourceNotFoundException("Job title not found."));
            employee.setJobTitle(jobTitle);
        } else {
            employee.setJobTitle(null);
        }
        if (dto.getManagerId() != null) {
            Employee manager = getEmployee(dto.getManagerId());
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }
    }

    private Employee getEmployee(Long id) {
        return employeeRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found."));
    }

    private EmployeeDto mapToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setUserId(employee.getUser().getId());
        dto.setUsername(employee.getUser().getUsername());
        dto.setEmail(employee.getUser().getEmail());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setHireDate(employee.getHireDate());
        dto.setStatus(employee.getStatus());
        dto.setContactNumber(employee.getContactNumber());
        dto.setAddress(employee.getAddress());
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
            dto.setDepartment(employee.getDepartment().getName());
        }
        if (employee.getJobTitle() != null) {
            dto.setJobTitleId(employee.getJobTitle().getId());
            dto.setJobTitle(employee.getJobTitle().getName());
        }
        if (employee.getManager() != null) {
            dto.setManagerId(employee.getManager().getId());
            dto.setManagerName(employee.getManager().getFirstName() + " " + employee.getManager().getLastName());
        }
        return dto;
    }
}
