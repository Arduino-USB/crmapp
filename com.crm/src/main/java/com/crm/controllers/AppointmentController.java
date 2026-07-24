package com.crm.controller;

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

    /**
     * Fetch a specific appointment by ID using ?id=... 
     * OR retrieve a paged list of appointments.
     * 
     * Examples:
     * - GET /api/appointments?id=1
     * - GET /api/appointments?page=0&size=10
     * - GET /api/appointments?page=0&size=10&sort=id,desc
     */
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

    /**
     * Add/Create a new appointment with standard validation.
     */
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@Valid @RequestBody Appointment appointment) {
        Appointment savedAppointment = appointmentRepo.save(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAppointment);
    }

    /**
     * Delete an appointment by ID.
     */
    @PostMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        if (!appointmentRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        appointmentRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
