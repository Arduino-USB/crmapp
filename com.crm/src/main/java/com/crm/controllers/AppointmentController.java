package com.crm.controllers;

import com.crm.entites.Appointment;
import com.crm.repos.AppointmentRepo;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AppointmentController {

    private final AppointmentRepo appointmentRepo;

    public AppointmentController(AppointmentRepo appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    @GetMapping
    public ResponseEntity<?> getAppointments(
            @RequestParam(required = false) Long id,
            @PageableDefault(size = 20) Pageable pageable) {

        if (id != null) {
            return appointmentRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }

        Page<Appointment> page = appointmentRepo.findAll(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentRepo.save(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment updatedDetails) {
        return appointmentRepo.findById(id)
                .map(existingAppointment -> {
                    if (updatedDetails.getTime() != null) {
                        existingAppointment.setTime(updatedDetails.getTime());
                    }
                    if (updatedDetails.getReason() != null) {
                        existingAppointment.setReason(updatedDetails.getReason());
                    }
                    Appointment saved = appointmentRepo.save(existingAppointment);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        if (!appointmentRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appointmentRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
