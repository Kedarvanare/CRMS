package com.crms.backend.controller;

import com.crms.backend.model.Criminal;
import com.crms.backend.repository.CriminalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/criminals")
@CrossOrigin(origins = "*")
public class CriminalController {

    private final CriminalRepository criminalRepository;

    public CriminalController(CriminalRepository criminalRepository) {
        this.criminalRepository = criminalRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllCriminals() {
        try {
            List<Criminal> criminals = criminalRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(criminals);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch criminals"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCriminal(@RequestBody Criminal criminal) {
        try {
            if (criminal.getStatus() == null || criminal.getStatus().isBlank()) {
                criminal.setStatus("Wanted");
            }
            if (criminal.getOfficer() == null || criminal.getOfficer().isBlank()) {
                criminal.setOfficer("Unknown");
            }
            criminalRepository.save(criminal);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to create criminal record"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCriminal(@PathVariable String id) {
        try {
            criminalRepository.deleteById(id);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to delete criminal record"));
        }
    }
}
