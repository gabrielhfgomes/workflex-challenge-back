package com.gabriel.workflexbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gabriel.workflexbackend.model.Employee;
import com.gabriel.workflexbackend.model.RiskEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkationResponseDTO {

    private Long id;

    private String employee;

    private String origin;

    private String destination;

    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate start;

    @JsonFormat(pattern = "dd/MM/yyyy", shape = JsonFormat.Shape.STRING)
    private LocalDate end;

    private Long workingDays;

    private RiskEnum risk;
}
