package com.gabriel.workflexbackend.mapper;

import com.gabriel.workflexbackend.dto.response.WorkationResponseDTO;
import com.gabriel.workflexbackend.model.Employee;
import com.gabriel.workflexbackend.model.Workation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface WorkationMapper {
    
    @Mapping(target = "employee", source = "employee.name")
    @Mapping(target = "start", source = "start", qualifiedByName = "mapToLocalDate")
    @Mapping(target = "end", source = "end", qualifiedByName = "mapToLocalDate")
    WorkationResponseDTO toDTO(Workation workation);
    
    @Mapping(target = "employee", ignore = true) // We'll handle employee mapping manually in service
    @Mapping(target = "start", source = "start", qualifiedByName = "mapToLocalDateTime")
    @Mapping(target = "end", source = "end", qualifiedByName = "mapToLocalDateTime")
    Workation toEntity(WorkationResponseDTO workationResponseDTO);
    
    // Helper method to map Employee to String
    default String mapEmployeeToString(Employee employee) {
        return employee != null ? employee.getName() : null;
    }
    
    // Helper method to map String to Employee (not used in this context, but for completeness)
    default Employee mapStringToEmployee(String employeeName) {
        if (employeeName == null) return null;
        Employee employee = new Employee();
        employee.setName(employeeName);
        return employee;
    }
    
    // Helper methods to convert between LocalDateTime and LocalDate
    @Named("mapToLocalDate")
    default LocalDate mapToLocalDate(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.toLocalDate() : null;
    }
    
    @Named("mapToLocalDateTime")
    default LocalDateTime mapToLocalDateTime(LocalDate localDate) {
        return localDate != null ? localDate.atStartOfDay() : null;
    }
}