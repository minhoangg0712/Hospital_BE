package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profiles")
    public List<UserDTO> getPatientProfiles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        // Lấy username từ SecurityContextHolder
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.getPatientProfiles(username);
    }

    @PutMapping("/update")
    public UserDTO updatePatientProfile(@RequestBody UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new RuntimeException("User not authenticated");
        }

        // Lấy username từ SecurityContextHolder
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.updatePatientProfile(username, userDTO);
    }
}
