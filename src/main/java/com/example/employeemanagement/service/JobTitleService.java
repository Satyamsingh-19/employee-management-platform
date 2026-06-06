package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.jobtitle.JobTitleDto;
import com.example.employeemanagement.entity.JobTitle;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.JobTitleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class JobTitleService {

    private final JobTitleRepository jobTitleRepository;
    private final AuditService auditService;

    public JobTitleService(JobTitleRepository jobTitleRepository, AuditService auditService) {
        this.jobTitleRepository = jobTitleRepository;
        this.auditService = auditService;
    }

    public long count() {
        return jobTitleRepository.count();
    }

    public List<JobTitleDto> findAll() {
        return jobTitleRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public JobTitleDto findById(Long id) {
        return mapToDto(getJobTitle(id));
    }

    public JobTitleDto createJobTitle(JobTitleDto dto) {
        JobTitle title = new JobTitle(dto.getName(), dto.getLevel(), dto.getDescription());
        JobTitle saved = jobTitleRepository.save(title);
        auditService.recordAction("JobTitle", saved.getId(), "CREATE", saved.getName());
        return mapToDto(saved);
    }

    public JobTitleDto updateJobTitle(Long id, JobTitleDto dto) {
        JobTitle title = getJobTitle(id);
        title.setName(dto.getName());
        title.setLevel(dto.getLevel());
        title.setDescription(dto.getDescription());
        JobTitle saved = jobTitleRepository.save(title);
        auditService.recordAction("JobTitle", saved.getId(), "UPDATE", "Updated " + saved.getName());
        return mapToDto(saved);
    }

    public void deleteJobTitle(Long id) {
        JobTitle title = getJobTitle(id);
        auditService.recordAction("JobTitle", title.getId(), "DELETE", "Removed " + title.getName());
        jobTitleRepository.delete(title);
    }

    private JobTitle getJobTitle(Long id) {
        return jobTitleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Job title not found."));
    }

    private JobTitleDto mapToDto(JobTitle title) {
        JobTitleDto dto = new JobTitleDto();
        dto.setId(title.getId());
        dto.setName(title.getName());
        dto.setLevel(title.getLevel());
        dto.setDescription(title.getDescription());
        return dto;
    }
}
