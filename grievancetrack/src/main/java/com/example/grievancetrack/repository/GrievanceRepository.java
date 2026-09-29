package com.example.grievancetrack.repository;

import com.example.grievancetrack.model.Grievance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
}