package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.audit.AuditLogDto;
import com.example.employeemanagement.entity.AuditLog;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.AuditLogRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLogDto> findAll() {
        return auditLogRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public List<AuditLogDto> findRecent(int limit) {
        return auditLogRepository.findTop6ByOrderByChangedAtDesc().stream()
            .limit(limit)
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public AuditLogDto findById(Long id) {
        return toDto(getAuditLog(id));
    }

    public AuditLogDto record(String entityName, Long entityId, String action, String details, String changedBy) {
        AuditLog log = new AuditLog(entityName, entityId, action, details, changedBy);
        return toDto(auditLogRepository.save(log));
    }

    public void recordAction(String entityName, Long entityId, String action, String details) {
        record(entityName, entityId, action, details, resolveCurrentUser());
    }

    private String resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
            && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "system";
    }

    private AuditLog getAuditLog(Long id) {
        return auditLogRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Audit record not found."));
    }

    private AuditLogDto toDto(AuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setId(log.getId());
        dto.setEntityName(log.getEntityName());
        dto.setEntityId(log.getEntityId());
        dto.setAction(log.getAction());
        dto.setDetails(log.getDetails());
        dto.setChangedBy(log.getChangedBy());
        dto.setChangedAt(log.getChangedAt());
        return dto;
    }
}
