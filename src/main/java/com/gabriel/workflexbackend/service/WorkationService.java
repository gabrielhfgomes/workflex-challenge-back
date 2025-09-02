package com.gabriel.workflexbackend.service;

import com.gabriel.workflexbackend.dto.response.WorkationResponseDTO;
import com.gabriel.workflexbackend.mapper.WorkationMapper;
import com.gabriel.workflexbackend.model.Employee;
import com.gabriel.workflexbackend.model.Workation;
import com.gabriel.workflexbackend.repository.EmployeeRepository;
import com.gabriel.workflexbackend.repository.WorkationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkationService {

    private final WorkationRepository workationRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkationMapper workationMapper;

    public List<WorkationResponseDTO> getAllWorkations() {
        return workationRepository.findAll().stream()
                .map(workationMapper::toDTO)
                .collect(Collectors.toList());
    }

    public WorkationResponseDTO getWorkationById(Long id) {
        Workation workation = workationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workation not found with id: " + id));
        return workationMapper.toDTO(workation);
    }

    public WorkationResponseDTO createWorkation(WorkationResponseDTO workationDTO) {
        // Find or create employee
        Employee employee = findOrCreateEmployee(workationDTO.getEmployee());
        
        // Convert DTO to entity
        Workation workation = workationMapper.toEntity(workationDTO);
        workation.setEmployee(employee);
        
        // Save and return
        Workation savedWorkation = workationRepository.save(workation);
        return workationMapper.toDTO(savedWorkation);
    }

    public WorkationResponseDTO updateWorkation(Long id, WorkationResponseDTO workationDTO) {
        Workation existingWorkation = workationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workation not found with id: " + id));

        // Find or create employee
        Employee employee = findOrCreateEmployee(workationDTO.getEmployee());

        // Update fields
        existingWorkation.setEmployee(employee);
        existingWorkation.setOrigin(workationDTO.getOrigin());
        existingWorkation.setDestination(workationDTO.getDestination());
        existingWorkation.setStart(workationDTO.getStart().atStartOfDay());
        existingWorkation.setEnd(workationDTO.getEnd().atTime(23, 59, 59));
        existingWorkation.setWorkingDays(workationDTO.getWorkingDays().intValue());
        existingWorkation.setRisk(workationDTO.getRisk());

        Workation savedWorkation = workationRepository.save(existingWorkation);
        return workationMapper.toDTO(savedWorkation);
    }

    public void deleteWorkation(Long id) {
        Workation workation = workationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workation not found with id: " + id));
        workationRepository.delete(workation);
    }

    private Employee findOrCreateEmployee(String employeeName) {
        // This is a simplified approach - in a real app you might want to search by name
        Employee employee = new Employee();
        employee.setName(employeeName);
        return employeeRepository.save(employee);
    }
}