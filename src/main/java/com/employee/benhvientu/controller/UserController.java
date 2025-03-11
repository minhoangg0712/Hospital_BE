package com.employee.benhvientu.controller;

import com.employee.benhvientu.dto.UserDTO;
import com.employee.benhvientu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.AccessDeniedException;

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

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new RuntimeException("Invalid authentication principal");
        }

        return userService.getPatientProfiles(username);
    }
    @GetMapping("/{id}")
    public UserDTO getUserProfileById(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserProfileById(id, username);
    }


    @PutMapping("/update")
    public UserDTO updatePatientProfile(@RequestBody UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            throw new AccessDeniedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();

        return userService.updatePatientProfile(username, userDTO);
    }
}
