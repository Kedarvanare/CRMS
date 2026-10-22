package com.crms.backend.controller;

import com.crms.backend.model.LoginHistory;
import com.crms.backend.repository.LoginHistoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/login-history")
@CrossOrigin(origins = "*")
public class LoginHistoryController {

    private final LoginHistoryRepository loginHistoryRepository;

    public LoginHistoryController(LoginHistoryRepository loginHistoryRepository) {
        this.loginHistoryRepository = loginHistoryRepository;
    }

    @GetMapping
    public ResponseEntity<?> getLoginHistory() {
        try {
            List<LoginHistory> history = loginHistoryRepository.findTop50ByOrderByCreatedAtDesc();
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch login history"));
        }
    }

    @PostMapping
    public ResponseEntity<?> recordLogin(@RequestBody LoginHistory entry) {
        try {
            if (entry.getLogout() == null || entry.getLogout().isBlank()) {
                entry.setLogout("N/A");
            }
            if (entry.getIp() == null || entry.getIp().isBlank()) {
                entry.setIp("127.0.0.1");
            }
            loginHistoryRepository.save(entry);
            return ResponseEntity.ok(Collections.singletonMap("success", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to log entry"));
        }
    }
}
