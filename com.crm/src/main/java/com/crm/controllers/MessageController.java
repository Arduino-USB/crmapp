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

    @GetMapping
    public ResponseEntity<?> getMessages(@RequestParam(required = false) Long id) {
        if (id != null) {
            return messageRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(messageRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@Valid @RequestBody Message message) {
        Message savedMessage = messageRepo.save(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMessage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Message> updateMessage(@PathVariable Long id, @Valid @RequestBody Message updatedMessage) {
        return messageRepo.findById(id)
                .map(existingMessage -> {
                    existingMessage.setTextList(updatedMessage.getTextList());
                    existingMessage.setDayMileStone(updatedMessage.getDayMileStone());
                    existingMessage.setBefore(updatedMessage.isBefore());
					existingMessage.setResponseIds(updatedMessage.getResponseIds());
                    Message saved = messageRepo.save(existingMessage);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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

    @GetMapping("/responses")
    public ResponseEntity<?> getResponses(@RequestParam(required = false) Long id) {
        if (id != null) {
            return responseRepo.findById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(responseRepo.findAll());
    }

    @PostMapping("/responses")
    public ResponseEntity<Response> createResponse(@Valid @RequestBody Response response) {
        Response savedResponse = responseRepo.save(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedResponse);
    }

    @PutMapping("/responses/{id}")
    public ResponseEntity<Response> updateResponse(@PathVariable Long id, @Valid @RequestBody Response updatedResponse) {
        return responseRepo.findById(id)
                .map(existingResponse -> {
                    existingResponse.setAction(updatedResponse.getAction());
                    existingResponse.setMode(updatedResponse.getMode());
                    Response saved = responseRepo.save(existingResponse);
                    return ResponseEntity.ok(saved);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

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
