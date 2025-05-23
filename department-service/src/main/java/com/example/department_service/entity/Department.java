package com.example.department_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "DEPARTMENT")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "TOTAL_MEMBER")
    private int totalMember;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "CREATED_DATE")
    private Date createdDate;
}
