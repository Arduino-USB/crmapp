package com.crm.controllers;

import com.crm.entites.SmsLog;
import com.crm.repos.SmsLogRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sms-logs")
@CrossOrigin
public class SmsLogController {

    private final SmsLogRepo smsLogRepo;

    public SmsLogController(SmsLogRepo smsLogRepo) {
        this.smsLogRepo = smsLogRepo;
    }

    // GET /api/sms-logs?page=0&size=20 or GET /api/sms-logs?id=12
    @GetMapping
    public ResponseEntity<?> getLogs(
            @RequestParam(required = false) Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (id != null) {
            return smsLogRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        Page<SmsLog> logPage = smsLogRepo.findAllByOrderByTimestampDesc(PageRequest.of(page, size));
        return ResponseEntity.ok(logPage);
    }

    // GET /api/sms-logs/appointment?appointmentId=101
    @GetMapping("/appointment")
    public ResponseEntity<List<SmsLog>> getLogsByAppointment(@RequestParam Long appointmentId) {
        return ResponseEntity.ok(smsLogRepo.findByAppointmentIdOrderByTimestampAsc(appointmentId));
    }

    // GET /api/sms-logs/user?userId=42
    @GetMapping("/user")
    public ResponseEntity<List<SmsLog>> getLogsByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(smsLogRepo.findByUserIdOrderByTimestampAsc(userId));
    }

    // POST /api/sms-logs/delete/{id} (Matching project delete convention)
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteLog(@PathVariable Long id) {
        if (!smsLogRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        smsLogRepo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
