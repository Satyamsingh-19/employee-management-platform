package com.example.employeemanagement.dto.jobtitle;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class JobTitleDto {

    private Long id;

    @NotBlank(message = "Job title name is required.")
    @Size(max = 100, message = "Job title name must not exceed 100 characters.")
    private String name;

    @Size(max = 50, message = "Level must not exceed 50 characters.")
    private String level;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    public JobTitleDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
