package com.gabriel.workflexbackend.service;

import com.gabriel.workflexbackend.dto.response.WorkationResponseDTO;
import com.gabriel.workflexbackend.mapper.WorkationMapper;
import com.gabriel.workflexbackend.model.Employee;
import com.gabriel.workflexbackend.model.RiskEnum;
import com.gabriel.workflexbackend.model.Workation;
import com.gabriel.workflexbackend.repository.EmployeeRepository;
import com.gabriel.workflexbackend.repository.WorkationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkationServiceTest {

    @Mock
    private WorkationRepository workationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private WorkationMapper workationMapper;

    @InjectMocks
    private WorkationService workationService;

    @Test
    void testGetAllWorkations() {
        // Arrange
        Workation workation1 = createSampleWorkation(1L, "John Doe", "Germany", "USA");
        Workation workation2 = createSampleWorkation(2L, "Jane Smith", "France", "Canada");
        List<Workation> workations = Arrays.asList(workation1, workation2);

        WorkationResponseDTO dto1 = createSampleWorkationDTO(1L, "John Doe", "Germany", "USA");
        WorkationResponseDTO dto2 = createSampleWorkationDTO(2L, "Jane Smith", "France", "Canada");

        when(workationRepository.findAll()).thenReturn(workations);
        when(workationMapper.toDTO(workation1)).thenReturn(dto1);
        when(workationMapper.toDTO(workation2)).thenReturn(dto2);

        // Act
        List<WorkationResponseDTO> result = workationService.getAllWorkations();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmployee()).isEqualTo("John Doe");
        assertThat(result.get(1).getEmployee()).isEqualTo("Jane Smith");
        verify(workationRepository, times(1)).findAll();
        verify(workationMapper, times(2)).toDTO(any(Workation.class));
    }

    @Test
    void testGetWorkationById() {
        // Arrange
        Long workationId = 1L;
        Workation workation = createSampleWorkation(workationId, "John Doe", "Germany", "USA");
        WorkationResponseDTO expectedDTO = createSampleWorkationDTO(workationId, "John Doe", "Germany", "USA");

        when(workationRepository.findById(workationId)).thenReturn(Optional.of(workation));
        when(workationMapper.toDTO(workation)).thenReturn(expectedDTO);

        // Act
        WorkationResponseDTO result = workationService.getWorkationById(workationId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(workationId);
        assertThat(result.getEmployee()).isEqualTo("John Doe");
        verify(workationRepository, times(1)).findById(workationId);
        verify(workationMapper, times(1)).toDTO(workation);
    }

    @Test
    void testGetWorkationByIdNotFound() {
        // Arrange
        Long workationId = 999L;
        when(workationRepository.findById(workationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workationService.getWorkationById(workationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Workation not found with id: " + workationId);

        verify(workationRepository, times(1)).findById(workationId);
        verify(workationMapper, never()).toDTO(any(Workation.class));
    }

    @Test
    void testCreateWorkation() {
        // Arrange
        WorkationResponseDTO inputDTO = createSampleWorkationDTO(null, "John Doe", "Germany", "USA");
        Employee employee = new Employee(1L, "John Doe");
        Workation inputWorkation = createSampleWorkation(null, "John Doe", "Germany", "USA");
        Workation savedWorkation = createSampleWorkation(1L, "John Doe", "Germany", "USA");
        WorkationResponseDTO expectedDTO = createSampleWorkationDTO(1L, "John Doe", "Germany", "USA");

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(workationMapper.toEntity(inputDTO)).thenReturn(inputWorkation);
        when(workationRepository.save(any(Workation.class))).thenReturn(savedWorkation);
        when(workationMapper.toDTO(savedWorkation)).thenReturn(expectedDTO);

        // Act
        WorkationResponseDTO result = workationService.createWorkation(inputDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmployee()).isEqualTo("John Doe");
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(workationMapper, times(1)).toEntity(inputDTO);
        verify(workationRepository, times(1)).save(any(Workation.class));
        verify(workationMapper, times(1)).toDTO(savedWorkation);
    }

    @Test
    void testUpdateWorkation() {
        // Arrange
        Long workationId = 1L;
        WorkationResponseDTO updateDTO = createSampleWorkationDTO(null, "John Doe Updated", "Germany", "Canada");
        Employee employee = new Employee(1L, "John Doe Updated");
        Workation existingWorkation = createSampleWorkation(workationId, "John Doe", "Germany", "USA");
        Workation updatedWorkation = createSampleWorkation(workationId, "John Doe Updated", "Germany", "Canada");
        WorkationResponseDTO expectedDTO = createSampleWorkationDTO(workationId, "John Doe Updated", "Germany", "Canada");

        when(workationRepository.findById(workationId)).thenReturn(Optional.of(existingWorkation));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        when(workationRepository.save(any(Workation.class))).thenReturn(updatedWorkation);
        when(workationMapper.toDTO(updatedWorkation)).thenReturn(expectedDTO);

        // Act
        WorkationResponseDTO result = workationService.updateWorkation(workationId, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(workationId);
        assertThat(result.getEmployee()).isEqualTo("John Doe Updated");
        assertThat(result.getDestination()).isEqualTo("Canada");
        verify(workationRepository, times(1)).findById(workationId);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(workationRepository, times(1)).save(existingWorkation);
        verify(workationMapper, times(1)).toDTO(updatedWorkation);
    }

    @Test
    void testUpdateWorkationNotFound() {
        // Arrange
        Long workationId = 999L;
        WorkationResponseDTO updateDTO = createSampleWorkationDTO(null, "John Doe", "Germany", "USA");

        when(workationRepository.findById(workationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workationService.updateWorkation(workationId, updateDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Workation not found with id: " + workationId);

        verify(workationRepository, times(1)).findById(workationId);
        verify(employeeRepository, never()).save(any(Employee.class));
        verify(workationRepository, never()).save(any(Workation.class));
    }

    @Test
    void testDeleteWorkation() {
        // Arrange
        Long workationId = 1L;
        Workation workation = createSampleWorkation(workationId, "John Doe", "Germany", "USA");

        when(workationRepository.findById(workationId)).thenReturn(Optional.of(workation));

        // Act
        workationService.deleteWorkation(workationId);

        // Assert
        verify(workationRepository, times(1)).findById(workationId);
        verify(workationRepository, times(1)).delete(workation);
    }

    @Test
    void testDeleteWorkationNotFound() {
        // Arrange
        Long workationId = 999L;
        when(workationRepository.findById(workationId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> workationService.deleteWorkation(workationId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Workation not found with id: " + workationId);

        verify(workationRepository, times(1)).findById(workationId);
        verify(workationRepository, never()).delete(any(Workation.class));
    }

    private Workation createSampleWorkation(Long id, String employeeName, String origin, String destination) {
        Employee employee = new Employee(id != null ? id : 1L, employeeName);
        Workation workation = new Workation();
        workation.setId(id);
        workation.setEmployee(employee);
        workation.setOrigin(origin);
        workation.setDestination(destination);
        workation.setStart(LocalDateTime.of(2024, 1, 1, 0, 0));
        workation.setEnd(LocalDateTime.of(2024, 12, 31, 23, 59));
        workation.setWorkingDays(100);
        workation.setRisk(RiskEnum.LOW);
        return workation;
    }

    private WorkationResponseDTO createSampleWorkationDTO(Long id, String employee, String origin, String destination) {
        WorkationResponseDTO dto = new WorkationResponseDTO();
        dto.setId(id);
        dto.setEmployee(employee);
        dto.setOrigin(origin);
        dto.setDestination(destination);
        dto.setStart(LocalDate.of(2024, 1, 1));
        dto.setEnd(LocalDate.of(2024, 12, 31));
        dto.setWorkingDays(100L);
        dto.setRisk(RiskEnum.LOW);
        return dto;
    }
}