package com.crms.backend.controller;

import com.crms.backend.dto.StatusUpdateRequest;
import com.crms.backend.model.Officer;
import com.crms.backend.repository.OfficerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/officers")
@CrossOrigin(origins = "*")
public class OfficerController {

    private final OfficerRepository officerRepository;

    public OfficerController(OfficerRepository officerRepository) {
        this.officerRepository = officerRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllOfficers() {
        try {
            List<Officer> officers = officerRepository.findAllByOrderByCreatedAtDesc();
            List<Map<String, Object>> result = new ArrayList<>();
            for (Officer o : officers) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", o.getId());
                map.put("name", o.getName());
                map.put("email", o.getEmail());
                map.put("department", o.getDepartment());
                map.put("status", o.getStatus());
                map.put("role", o.getRole());
                result.add(map);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch officers"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createOfficer(@RequestBody Officer officer) {
        try {
            if (officer.getDepartment() == null) {
                officer.setDepartment("");
            }
            if (officer.getStatus() == null || officer.getStatus().isBlank()) {
                officer.setStatus("Active");
            }
            if (officer.getRole() == null || officer.getRole().isBlank()) {
                officer.setRole("Officer");
            }
            officerRepository.save(officer);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to create officer"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOfficerStatus(@PathVariable String id, @RequestBody StatusUpdateRequest req) {
        try {
            Optional<Officer> offOpt = officerRepository.findById(id);
            if (offOpt.isPresent()) {
                Officer o = offOpt.get();
                o.setStatus(req.getStatus());
                officerRepository.save(o);
                return ResponseEntity.ok(Collections.singletonMap("success", true));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Officer not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to update officer status"));
        }
    }
}
