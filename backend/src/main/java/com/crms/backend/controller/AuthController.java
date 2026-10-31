package com.crms.backend.controller;

import com.crms.backend.dto.LoginRequest;
import com.crms.backend.model.Admin;
import com.crms.backend.model.Officer;
import com.crms.backend.repository.AdminRepository;
import com.crms.backend.repository.OfficerRepository;
import com.crms.backend.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AdminRepository adminRepository;
    private final OfficerRepository officerRepository;
    private final JwtUtils jwtUtils;

    public AuthController(AdminRepository adminRepository, OfficerRepository officerRepository, JwtUtils jwtUtils) {
        this.adminRepository = adminRepository;
        this.officerRepository = officerRepository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginReq) {
        try {
            Map<String, Object> response = new HashMap<>();
            String type = loginReq.getType();
            String email = loginReq.getEmail();
            String password = loginReq.getPassword();

            if ("admin".equalsIgnoreCase(type)) {
                Optional<Admin> adminOpt = adminRepository.findByEmailAndPassword(email, password);
                if (adminOpt.isPresent()) {
                    Admin admin = adminOpt.get();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("name", admin.getName());
                    userData.put("role", "Admin");
                    userData.put("email", admin.getEmail());

                    String token = jwtUtils.generateToken(admin.getEmail(), "Admin", admin.getName());

                    response.put("success", true);
                    response.put("token", token);
                    response.put("user", userData);
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "Invalid admin credentials");
                    return ResponseEntity.ok(response);
                }
            } else {
                Optional<Officer> officerOpt = officerRepository.findByEmailAndPassword(email, password);
                if (officerOpt.isPresent()) {
                    Officer officer = officerOpt.get();
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("id", officer.getId());
                    userData.put("name", officer.getName());
                    userData.put("email", officer.getEmail());
                    userData.put("department", officer.getDepartment());
                    userData.put("status", officer.getStatus());
                    userData.put("role", "Officer");

                    String token = jwtUtils.generateToken(officer.getEmail(), "Officer", officer.getName());

                    response.put("success", true);
                    response.put("token", token);
                    response.put("user", userData);
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "Invalid officer credentials");
                    return ResponseEntity.ok(response);
                }
            }
        } catch (Exception e) {
            Map<String, Object> errResponse = new HashMap<>();
            errResponse.put("success", false);
            errResponse.put("message", "Server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
        }
    }
}
