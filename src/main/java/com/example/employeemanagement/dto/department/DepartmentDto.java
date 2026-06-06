package com.example.employeemanagement.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DepartmentDto {

    private Long id;

    @NotBlank(message = "Department name is required.")
    @Size(max = 100, message = "Department name must not exceed 100 characters.")
    private String name;

    @NotBlank(message = "Department code is required.")
    @Size(max = 20, message = "Department code must not exceed 20 characters.")
    private String code;

    @Size(max = 500, message = "Description must not exceed 500 characters.")
    private String description;

    public DepartmentDto() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
