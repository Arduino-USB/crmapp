package com.crm.controllers;

import com.crm.EntityMerge;
import com.crm.entites.Appointment;
import com.crm.repos.AppointmentRepo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AppointmentController {

    private final AppointmentRepo appointmentRepo;

    public AppointmentController(AppointmentRepo appointmentRepo) {
        this.appointmentRepo = appointmentRepo;
    }

    @GetMapping("/get_appointment_by_id")
    public Appointment getAppointmentById(@RequestParam Long id) {
        Optional<Appointment> appointment = appointmentRepo.findById(id);

        if (appointment.isPresent()) {
            return appointment.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment does not exist");
        }
    }
	
	@GetMapping("/search")
    public Page<Appointment> searchAppointmentsByPartName(@RequestParam String partName) {
        // Pageable.unpaged() fetches all matching records and packs them into a single Page response
        return appointmentRepo.searchByUserNameContainingIgnoreCase(partName, Pageable.unpaged());
    }

    @PostMapping("/create_appointment")
    public Appointment createAppointment(@Valid @RequestBody Appointment appointment) {
        return appointmentRepo.save(appointment);
    }

    @GetMapping("/get_all_appointments")
    public Page<Appointment> getAllAppointments(@RequestParam int page, @RequestParam(defaultValue = "9") int pageSize) {
        return appointmentRepo.findAll(PageRequest.of(page, pageSize));
    }

    @PutMapping("/update_appointment")
    public Appointment updateAppointment(@RequestBody Appointment updatedDetails) {
        Optional<Appointment> originalAppointment = appointmentRepo.findById(updatedDetails.getId());

        if (!originalAppointment.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment does not exist");
        }

        Appointment merged = EntityMerge.merge(originalAppointment.get(), updatedDetails);
        return appointmentRepo.save(merged);
    }

    @DeleteMapping("/delete_appointment")
    public ResponseEntity<String> deleteAppointment(@RequestParam Long id) {
        if (!appointmentRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment does not exist");
        }

        appointmentRepo.deleteById(id);
        return ResponseEntity.ok("Appointment deleted successfully");
    }
}
