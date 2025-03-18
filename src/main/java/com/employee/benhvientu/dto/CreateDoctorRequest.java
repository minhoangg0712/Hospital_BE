package com.employee.benhvientu.dto;

import lombok.Data;

@Data
public class CreateDoctorRequest {
    private String username;
    private String password;
    private Integer departmentId; // Thêm trường departmentId

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
} 