package com.example.grievancetrack.controller;

import com.example.grievancetrack.model.Grievance;
import com.example.grievancetrack.service.GrievanceService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceService service;

    public GrievanceController(GrievanceService service) {
        this.service = service;
    }

    @PostMapping
    public Grievance createGrievance(
            @RequestBody Grievance grievance) {

        return service.createGrievance(grievance);
    }

    @GetMapping
    public List<Grievance> getAllGrievances() {

        return service.getAllGrievances();
    }

    @GetMapping("/{id}")
    public Grievance getGrievance(
            @PathVariable Long id) {

        return service.getGrievance(id);
    }

    @PutMapping("/{id}")
    public Grievance updateGrievance(
            @PathVariable Long id,
            @RequestBody Grievance grievance) {

        return service.updateGrievance(id, grievance);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteGrievance(
            @PathVariable Long id) {

        service.deleteGrievance(id);

        return ResponseEntity.ok("Grievance deleted successfully");
    }

    @PutMapping("/{id}/rate")
    public Grievance rateGrievance(
            @PathVariable Long id,
            @RequestParam Integer rating) {

        return service.rateGrievance(id, rating);
    }
}