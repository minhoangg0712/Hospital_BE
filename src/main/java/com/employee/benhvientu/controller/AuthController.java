package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.RegisterRequest;
import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> loginRequest) {
        String token = authService.authenticate(loginRequest.get("username"), loginRequest.get("password"));
        return Map.of("token", token);
    }
    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }
}
