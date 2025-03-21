package com.employee.benhvientu.controller;

import com.employee.benhvientu.entity.Appointment;
import com.employee.benhvientu.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Appointment appointment,
            @RequestParam boolean forSelf,
            Authentication authentication) {
        String username = authentication.getName();
        Appointment createdAppointment = appointmentService.createAppointment(appointment, username, forSelf);
        return ResponseEntity.ok(createdAppointment);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> listAppointments(Authentication authentication) {
        String username = authentication.getName();
        List<Appointment> appointments = appointmentService.listAppointments(username);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable int id, @RequestBody Appointment appointment, Authentication authentication) {
        String username = authentication.getName();
        Appointment updatedAppointment = appointmentService.updateAppointment(id, appointment, username);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable int id, Authentication authentication) {
        String username = authentication.getName();
        appointmentService.deleteAppointment(id, username);
        return ResponseEntity.noContent().build();
    }
}