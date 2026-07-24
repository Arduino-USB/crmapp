package com.crm.controllers;

import com.crm.entites.Message;
import com.crm.entites.Response;
import com.crm.repos.MessageRepo;
import com.crm.repos.ResponseRepo;

import com.crm.enums.ResponseAction;
import com.crm.enums.MatchMode;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MessageController {

    private final MessageRepo messageRepo;
    private final ResponseRepo responseRepo;

    public MessageController(MessageRepo messageRepo, ResponseRepo responseRepo) {
        this.messageRepo = messageRepo;
        this.responseRepo = responseRepo;
    }

    // ==========================================
    // MESSAGE ENDPOINTS
    // ==========================================

    /**
     * Get all messages or get a specific message using query parameter ?id=...
     * Example: GET /api/messages or GET /api/messages?id=1
     */
    @GetMapping
    public ResponseEntity<?> getMessages(@RequestParam(required = false) Long id) {
        if (id != null) {
            return messageRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(messageRepo.findAll());
    }

    /**
     * Add/Create a new message
     * Example: POST /api/messages
     */
    @PostMapping
    public ResponseEntity<Message> createMessage(@Valid @RequestBody Message message) {
        Message savedMessage = messageRepo.save(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    /**
     * Delete a message by ID via POST
     * Example: POST /api/messages/delete/1
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        if (!messageRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        messageRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ==========================================
    // RESPONSE ENDPOINTS
    // ==========================================

    /**
     * Get all responses or get a specific response using query parameter ?id=...
     * Example: GET /api/messages/responses or GET /api/messages/responses?id=1
     */
    @GetMapping("/responses")
    public ResponseEntity<?> getResponses(@RequestParam(required = false) Long id) {
        if (id != null) {
            return responseRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(responseRepo.findAll());
    }

    /**
     * Add/Create a new response
     * Example: POST /api/messages/responses
     */
    @PostMapping("/responses")
    public ResponseEntity<Response> createResponse(@Valid @RequestBody Response response) {
        Response savedResponse = responseRepo.save(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
    }

    /**
     * Delete a response by ID via POST
     * Example: POST /api/messages/responses/delete/1
     */
    @PostMapping("/responses/delete/{id}")
    public ResponseEntity<Void> deleteResponse(@PathVariable Long id) {
        if (!responseRepo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        responseRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
		
	@GetMapping("/responses/get_actions")
	public ResponseAction[] getResponseActions() {
		return ResponseAction.values();
	}
		
	@GetMapping("/responses/match_modes")
	public MatchMode[] getMatchModes() {
		return MatchMode.values();
	}
}
