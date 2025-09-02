package com.gabriel.workflexbackend.controller;

import com.gabriel.workflexbackend.dto.response.WorkationResponseDTO;
import com.gabriel.workflexbackend.service.WorkationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workflex/workation")
@RequiredArgsConstructor
public class WorkationController {

    private final WorkationService workationService;

    @GetMapping
    public ResponseEntity<List<WorkationResponseDTO>> getAllWorkations() {
        return ResponseEntity.ok(workationService.getAllWorkations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkationResponseDTO> getWorkationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(workationService.getWorkationById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<WorkationResponseDTO> createWorkation(@RequestBody WorkationResponseDTO workationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workationService.createWorkation(workationDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<WorkationResponseDTO> updateWorkation(@PathVariable("id") Long id, @RequestBody WorkationResponseDTO workationDTO) {
        return ResponseEntity.ok(workationService.updateWorkation(id, workationDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkation(@PathVariable("id") Long id) {
        workationService.deleteWorkation(id);
    }
}
