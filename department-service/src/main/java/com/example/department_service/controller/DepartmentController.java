package com.example.department_service.controller;

import com.example.department_service.dto.DepartmentDTO;
import com.example.department_service.entity.Department;
import com.example.department_service.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public List<DepartmentDTO> getDepartments() {
        List<Department> departments = departmentService.getAllDepartments();
        return departments.stream()
                .map(department -> new DepartmentDTO(department.getName(), department.getCreatedDate()))
                .toList();
    }
}
