package com.gabriel.workflexbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor // Required for JPA/Hibernate
@AllArgsConstructor
public class Workation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private String origin;

    private String destination;

    private LocalDateTime start;

    @Column(name = "end_date") // Rename column to avoid 'end' reserved keyword
    private LocalDateTime end;

    private Integer workingDays;

    @Enumerated(EnumType.STRING)
    private RiskEnum risk;
}
