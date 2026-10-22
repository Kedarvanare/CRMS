package com.crms.backend.controller;

import com.crms.backend.dto.StatusUpdateRequest;
import com.crms.backend.model.CaseRecord;
import com.crms.backend.repository.CaseRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*")
public class CaseController {

    private final CaseRepository caseRepository;

    public CaseController(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllCases() {
        try {
            List<CaseRecord> cases = caseRepository.findAllByOrderByCreatedAtDesc();
            return ResponseEntity.ok(cases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch cases"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createCase(@RequestBody CaseRecord newCase) {
        try {
            if (newCase.getCriminal() == null || newCase.getCriminal().isBlank()) {
                newCase.setCriminal("Unknown");
            }
            if (newCase.getStatus() == null || newCase.getStatus().isBlank()) {
                newCase.setStatus("Pending");
            }
            caseRepository.save(newCase);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to create case"));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateCaseStatus(@PathVariable String id, @RequestBody StatusUpdateRequest req) {
        try {
            Optional<CaseRecord> caseOpt = caseRepository.findById(id);
            if (caseOpt.isPresent()) {
                CaseRecord c = caseOpt.get();
                c.setStatus(req.getStatus());
                caseRepository.save(c);
                return ResponseEntity.ok(Collections.singletonMap("success", true));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "Case not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to update case"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCase(@PathVariable String id) {
        try {
            caseRepository.deleteById(id);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to delete case"));
        }
    }
}
