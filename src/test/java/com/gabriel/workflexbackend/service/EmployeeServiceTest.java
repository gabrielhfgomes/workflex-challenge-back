package com.gabriel.workflexbackend.service;

import com.gabriel.workflexbackend.model.Employee;
import com.gabriel.workflexbackend.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void testGetAllEmployees() {
        // Arrange
        Employee employee1 = new Employee(1L, "John Doe");
        Employee employee2 = new Employee(2L, "Jane Smith");
        List<Employee> employees = Arrays.asList(employee1, employee2);

        when(employeeRepository.findAll()).thenReturn(employees);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(1).getName()).isEqualTo("Jane Smith");
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    void testGetEmployeeById() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = new Employee(employeeId, "John Doe");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        Employee result = employeeService.getEmployeeById(employeeId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(employeeId);
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void testGetEmployeeByIdNotFound() {
        // Arrange
        Long employeeId = 999L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.getEmployeeById(employeeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Employee not found with id: " + employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void testCreateEmployee() {
        // Arrange
        Employee inputEmployee = new Employee(null, "John Doe");
        Employee savedEmployee = new Employee(1L, "John Doe");

        when(employeeRepository.save(inputEmployee)).thenReturn(savedEmployee);

        // Act
        Employee result = employeeService.createEmployee(inputEmployee);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(employeeRepository, times(1)).save(inputEmployee);
    }

    @Test
    void testUpdateEmployee() {
        // Arrange
        Long employeeId = 1L;
        Employee existingEmployee = new Employee(employeeId, "John Doe");
        Employee updateDetails = new Employee(null, "John Doe Updated");
        Employee updatedEmployee = new Employee(employeeId, "John Doe Updated");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.updateEmployee(employeeId, updateDetails);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(employeeId);
        assertThat(result.getName()).isEqualTo("John Doe Updated");
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    @Test
    void testUpdateEmployeeNotFound() {
        // Arrange
        Long employeeId = 999L;
        Employee updateDetails = new Employee(null, "John Doe Updated");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployee(employeeId, updateDetails))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Employee not found with id: " + employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testDeleteEmployee() {
        // Arrange
        Long employeeId = 1L;
        Employee employee = new Employee(employeeId, "John Doe");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        employeeService.deleteEmployee(employeeId);

        // Assert
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void testDeleteEmployeeNotFound() {
        // Arrange
        Long employeeId = 999L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.deleteEmployee(employeeId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Employee not found with id: " + employeeId);

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}