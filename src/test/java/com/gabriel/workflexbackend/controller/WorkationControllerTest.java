package com.gabriel.workflexbackend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabriel.workflexbackend.dto.response.WorkationResponseDTO;
import com.gabriel.workflexbackend.model.RiskEnum;
import com.gabriel.workflexbackend.service.WorkationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkationController.class)
class WorkationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkationService workationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllWorkations() throws Exception {
        // Arrange
        WorkationResponseDTO workation1 = createSampleWorkationDTO(1L, "John Doe", "Germany", "USA");
        WorkationResponseDTO workation2 = createSampleWorkationDTO(2L, "Jane Smith", "France", "Canada");
        List<WorkationResponseDTO> workations = Arrays.asList(workation1, workation2);

        when(workationService.getAllWorkations()).thenReturn(workations);

        // Act & Assert
        mockMvc.perform(get("/workflex/workation"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].employee").value("John Doe"))
                .andExpect(jsonPath("$[0].origin").value("Germany"))
                .andExpect(jsonPath("$[0].destination").value("USA"))
                .andExpect(jsonPath("$[0].start").value("01/01/2024"))
                .andExpect(jsonPath("$[0].end").value("31/12/2024"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].employee").value("Jane Smith"))
                .andExpect(jsonPath("$[1].start").value("01/01/2024"))
                .andExpect(jsonPath("$[1].end").value("31/12/2024"));

        verify(workationService, times(1)).getAllWorkations();
    }

    @Test
    void testGetWorkationById() throws Exception {
        // Arrange
        WorkationResponseDTO workation = createSampleWorkationDTO(1L, "John Doe", "Germany", "USA");
        when(workationService.getWorkationById(1L)).thenReturn(workation);

        // Act & Assert
        mockMvc.perform(get("/workflex/workation/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employee").value("John Doe"))
                .andExpect(jsonPath("$.origin").value("Germany"))
                .andExpect(jsonPath("$.destination").value("USA"))
                .andExpect(jsonPath("$.start").value("01/01/2024"))
                .andExpect(jsonPath("$.end").value("31/12/2024"));

        verify(workationService, times(1)).getWorkationById(1L);
    }

    @Test
    void testCreateWorkation() throws Exception {
        // Arrange
        WorkationResponseDTO inputWorkation = createSampleWorkationDTO(null, "John Doe", "Germany", "USA");
        WorkationResponseDTO createdWorkation = createSampleWorkationDTO(1L, "John Doe", "Germany", "USA");

        when(workationService.createWorkation(any(WorkationResponseDTO.class))).thenReturn(createdWorkation);

        // Act & Assert
        mockMvc.perform(post("/workflex/workation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWorkation)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employee").value("John Doe"))
                .andExpect(jsonPath("$.origin").value("Germany"))
                .andExpect(jsonPath("$.destination").value("USA"))
                .andExpect(jsonPath("$.start").value("01/01/2024"))
                .andExpect(jsonPath("$.end").value("31/12/2024"));

        verify(workationService, times(1)).createWorkation(any(WorkationResponseDTO.class));
    }

    @Test
    void testUpdateWorkation() throws Exception {
        // Arrange
        WorkationResponseDTO inputWorkation = createSampleWorkationDTO(null, "John Doe Updated", "Germany", "Canada");
        WorkationResponseDTO updatedWorkation = createSampleWorkationDTO(1L, "John Doe Updated", "Germany", "Canada");

        when(workationService.updateWorkation(eq(1L), any(WorkationResponseDTO.class))).thenReturn(updatedWorkation);

        // Act & Assert
        mockMvc.perform(put("/workflex/workation/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputWorkation)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.employee").value("John Doe Updated"))
                .andExpect(jsonPath("$.destination").value("Canada"))
                .andExpect(jsonPath("$.start").value("01/01/2024"))
                .andExpect(jsonPath("$.end").value("31/12/2024"));

        verify(workationService, times(1)).updateWorkation(eq(1L), any(WorkationResponseDTO.class));
    }

    @Test
    void testDeleteWorkation() throws Exception {
        // Arrange
        doNothing().when(workationService).deleteWorkation(1L);

        // Act & Assert
        mockMvc.perform(delete("/workflex/workation/1"))
                .andExpect(status().isNoContent());

        verify(workationService, times(1)).deleteWorkation(1L);
    }

    @Test
    void testGetWorkationByIdNotFound() throws Exception {
        // Arrange
        when(workationService.getWorkationById(999L))
                .thenThrow(new IllegalArgumentException("Workation not found with id: 999"));

        // Act & Assert
        mockMvc.perform(get("/workflex/workation/999"))
                .andExpect(status().isInternalServerError());

        verify(workationService, times(1)).getWorkationById(999L);
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