package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.department.DepartmentDto;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final AuditService auditService;

    public DepartmentService(DepartmentRepository departmentRepository, AuditService auditService) {
        this.departmentRepository = departmentRepository;
        this.auditService = auditService;
    }

    public long count() {
        return departmentRepository.count();
    }

    public List<DepartmentDto> findAll() {
        return departmentRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public DepartmentDto findById(Long id) {
        return mapToDto(getDepartment(id));
    }

    public DepartmentDto createDepartment(DepartmentDto dto) {
        Department department = new Department(dto.getName(), dto.getCode(), dto.getDescription());
        Department saved = departmentRepository.save(department);
        auditService.recordAction("Department", saved.getId(), "CREATE", saved.getName() + " (" + saved.getCode() + ")");
        return mapToDto(saved);
    }

    public DepartmentDto updateDepartment(Long id, DepartmentDto dto) {
        Department department = getDepartment(id);
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());
        Department saved = departmentRepository.save(department);
        auditService.recordAction("Department", saved.getId(), "UPDATE", "Updated " + saved.getName());
        return mapToDto(saved);
    }

    public void deleteDepartment(Long id) {
        Department department = getDepartment(id);
        auditService.recordAction("Department", department.getId(), "DELETE", "Removed " + department.getName());
        departmentRepository.delete(department);
    }

    private Department getDepartment(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Department not found."));
    }

    private DepartmentDto mapToDto(Department department) {
        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        dto.setDescription(department.getDescription());
        return dto;
    }
}
