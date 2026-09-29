package com.example.grievancetrack.service;

import com.example.grievancetrack.model.Grievance;
import com.example.grievancetrack.repository.GrievanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GrievanceService {

    private final GrievanceRepository repository;

    public GrievanceService(GrievanceRepository repository) {
        this.repository = repository;
    }

    public Grievance createGrievance(Grievance grievance) {

        setDepartmentAndSla(grievance);

        grievance.setStatus("OPEN");
        grievance.setStage("RECEIVED");
        grievance.setCreatedAt(LocalDateTime.now());

        return repository.save(grievance);
    }

    private void setDepartmentAndSla(Grievance grievance) {

        String category = grievance.getCategory().toLowerCase();

        if (category.equals("road")) {
            grievance.setDepartment("Road Department");
            grievance.setSlaDays(3);

        } else if (category.equals("water")) {
            grievance.setDepartment("Water Department");
            grievance.setSlaDays(2);

        } else if (category.equals("garbage")) {
            grievance.setDepartment("Sanitation Department");
            grievance.setSlaDays(1);

        } else if (category.equals("streetlight")) {
            grievance.setDepartment("Electrical Department");
            grievance.setSlaDays(2);

        } else {
            throw new RuntimeException(
                    "Invalid category. Use Road, Water, Garbage or Streetlight"
            );
        }
    }

    public List<Grievance> getAllGrievances() {

        List<Grievance> list = repository.findAll();

        for (Grievance grievance : list) {
            checkEscalation(grievance);
        }

        return repository.saveAll(list);
    }

    public Grievance getGrievance(Long id) {

        Grievance grievance = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        checkEscalation(grievance);

        return repository.save(grievance);
    }

    public Grievance updateGrievance(Long id, Grievance newGrievance) {

        Grievance grievance = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        grievance.setStatus(newGrievance.getStatus());
        grievance.setStage(newGrievance.getStage());

        if ("RESOLVED".equalsIgnoreCase(newGrievance.getStatus())) {
            grievance.setResolvedAt(LocalDateTime.now());
        }

        if (newGrievance.getRating() != null) {
            grievance.setRating(newGrievance.getRating());
        }

        return repository.save(grievance);
    }

    public void deleteGrievance(Long id) {

        if (!repository.existsById(id)) {
            throw new RuntimeException("Grievance not found");
        }

        repository.deleteById(id);
    }

    private void checkEscalation(Grievance grievance) {

        if ("RESOLVED".equalsIgnoreCase(grievance.getStatus())) {
            return;
        }

        LocalDateTime deadline = grievance.getCreatedAt()
                .plusDays(grievance.getSlaDays());

        if (LocalDateTime.now().isAfter(deadline)
                && grievance.getEscalationOfficer() == null) {

            grievance.setStatus("ESCALATED");
            grievance.setStage("ESCALATED");
            grievance.setEscalationOfficer("Senior Municipal Officer");
            grievance.setEscalatedAt(LocalDateTime.now());
        }
    }

    public Grievance rateGrievance(Long id, Integer rating) {

        Grievance grievance = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grievance not found"));

        if (!"RESOLVED".equalsIgnoreCase(grievance.getStatus())) {
            throw new RuntimeException(
                    "Grievance can be rated only after resolution"
            );
        }

        if (rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        grievance.setRating(rating);

        return repository.save(grievance);
    }
}