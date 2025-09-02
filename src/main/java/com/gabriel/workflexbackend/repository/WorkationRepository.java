package com.gabriel.workflexbackend.repository;

import com.gabriel.workflexbackend.model.Workation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkationRepository extends JpaRepository<Workation, Long> {
}
