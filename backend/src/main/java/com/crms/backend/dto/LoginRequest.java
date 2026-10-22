package com.crms.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
    private String type; // "admin" or "officer"
}
