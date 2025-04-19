package com.example.department_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class DepartmentDTO {
    private String name;
    private Date createdDate;
}
