package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.leave.LeaveRequestDto;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.LeaveRequest;
import com.example.employeemanagement.exception.BadRequestException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.LeaveRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveService {

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    public LeaveService(LeaveRequestRepository leaveRequestRepository,
                        EmployeeRepository employeeRepository,
                        AuditService auditService) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    public long count() {
        return leaveRequestRepository.count();
    }

    public List<LeaveRequestDto> findAll() {
        return leaveRequestRepository.findAllWithEmployee().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public LeaveRequestDto findById(Long id) {
        return mapToDto(getLeaveRequest(id));
    }

    public LeaveRequestDto createLeaveRequest(LeaveRequestDto dto) {
        validateDates(dto);
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found."));
        LeaveRequest entity = new LeaveRequest();
        entity.setEmployee(employee);
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setType(dto.getType());
        entity.setReason(dto.getReason());
        LeaveRequest saved = leaveRequestRepository.save(entity);
        auditService.recordAction("LeaveRequest", saved.getId(), "CREATE",
            employee.getFirstName() + " " + employee.getLastName() + " requested " + saved.getType());
        return mapToDto(saved);
    }

    public LeaveRequestDto updateLeaveRequest(Long id, LeaveRequestDto dto) {
        validateDates(dto);
        LeaveRequest request = getLeaveRequest(id);
        request.setStartDate(dto.getStartDate());
        request.setEndDate(dto.getEndDate());
        request.setType(dto.getType());
        request.setReason(dto.getReason());
        if (dto.getStatus() != null) {
            request.setStatus(dto.getStatus());
        }
        LeaveRequest saved = leaveRequestRepository.save(request);
        auditService.recordAction("LeaveRequest", saved.getId(), "UPDATE", "Updated leave request");
        return mapToDto(saved);
    }

    public LeaveRequestDto approveLeave(Long id, String approver) {
        LeaveRequest request = getLeaveRequest(id);
        request.setStatus("APPROVED");
        request.setApprovedBy(approver);
        request.setApprovedAt(Instant.now());
        LeaveRequest saved = leaveRequestRepository.save(request);
        auditService.recordAction("LeaveRequest", saved.getId(), "APPROVE", "Approved by " + approver);
        return mapToDto(saved);
    }

    public LeaveRequestDto rejectLeave(Long id, String approver) {
        LeaveRequest request = getLeaveRequest(id);
        request.setStatus("REJECTED");
        request.setApprovedBy(approver);
        request.setApprovedAt(Instant.now());
        LeaveRequest saved = leaveRequestRepository.save(request);
        auditService.recordAction("LeaveRequest", saved.getId(), "REJECT", "Rejected by " + approver);
        return mapToDto(saved);
    }

    public void deleteLeaveRequest(Long id) {
        LeaveRequest request = getLeaveRequest(id);
        auditService.recordAction("LeaveRequest", request.getId(), "DELETE", "Deleted leave request");
        leaveRequestRepository.delete(request);
    }

    private void validateDates(LeaveRequestDto dto) {
        if (dto.getStartDate() != null && dto.getEndDate() != null
            && dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new BadRequestException("End date must be on or after start date.");
        }
    }

    private LeaveRequest getLeaveRequest(Long id) {
        return leaveRequestRepository.findByIdWithEmployee(id)
            .orElseThrow(() -> new ResourceNotFoundException("Leave request not found."));
    }

    private LeaveRequestDto mapToDto(LeaveRequest request) {
        LeaveRequestDto dto = new LeaveRequestDto();
        dto.setId(request.getId());
        dto.setEmployeeId(request.getEmployee().getId());
        dto.setEmployeeName(request.getEmployee().getFirstName() + " " + request.getEmployee().getLastName());
        dto.setStartDate(request.getStartDate());
        dto.setEndDate(request.getEndDate());
        dto.setType(request.getType());
        dto.setStatus(request.getStatus());
        dto.setReason(request.getReason());
        dto.setApprovedBy(request.getApprovedBy());
        return dto;
    }
}
