package com.crm.controllers;

import com.crm.EntityMerge;
import com.crm.entites.Message;
import com.crm.entites.Response;
import com.crm.enums.MatchMode;
import com.crm.enums.ResponseAction;
import com.crm.repos.MessageRepo;
import com.crm.repos.ResponseRepo;
import com.crm.embeds.TextBlock.VarType;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MessageController {

    private final MessageRepo messageRepo;
    private final ResponseRepo responseRepo;

    public MessageController(
            MessageRepo messageRepo,
            ResponseRepo responseRepo) {
        this.messageRepo = messageRepo;
        this.responseRepo = responseRepo;
    }

    // ============================
    // MESSAGES
    // ============================

    @GetMapping("/get_message_by_id")
    public Message getMessageById(@RequestParam Long id) {
        Optional<Message> message = messageRepo.findById(id);

        if (message.isPresent()) {
            return message.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message does not exist");
        }
    }

    @GetMapping("/get_all_messages")
    public List<Message> getAllMessages() {
        return messageRepo.findAll();
    }

    @PostMapping("/create_message")
    public Message createMessage(@Valid @RequestBody Message message) {
        return messageRepo.save(message);
    }

    @PutMapping("/update_message")
    public Message updateMessage(@Valid @RequestBody Message updatedMessage) {
        Optional<Message> originalMessage = messageRepo.findById(updatedMessage.getId());

        if (!originalMessage.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message does not exist");
        }

        Message merged = EntityMerge.merge(originalMessage.get(), updatedMessage);
        return messageRepo.save(merged);
    }

    @DeleteMapping("/delete_message")
    public ResponseEntity<String> deleteMessage(@RequestParam Long id) {
        if (!messageRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message does not exist");
        }

        messageRepo.deleteById(id);
        return ResponseEntity.ok("Message deleted successfully");
    }

    // ============================
    // RESPONSES
    // ============================

    @GetMapping("/responses/get_response_by_id")
    public Response getResponseById(@RequestParam Long id) {
        Optional<Response> response = responseRepo.findById(id);

        if (response.isPresent()) {
            return response.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Response does not exist");
        }
    }

    @GetMapping("/responses/get_all_responses")
    public List<Response> getAllResponses() {
        return responseRepo.findAll();
    }

    @PostMapping("/responses/create_response")
    public Response createResponse(@Valid @RequestBody Response response) {
        return responseRepo.save(response);
    }

    @PutMapping("/responses/update_response")
    public Response updateResponse(@Valid @RequestBody Response updatedResponse) {
        Optional<Response> originalResponse = responseRepo.findById(updatedResponse.getId());

        if (!originalResponse.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Response does not exist");
        }

        Response merged = EntityMerge.merge(originalResponse.get(), updatedResponse);
        return responseRepo.save(merged);
    }

    @DeleteMapping("/responses/delete_response")
    public ResponseEntity<String> deleteResponse(@RequestParam Long id) {
        if (!responseRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Response does not exist");
        }

        responseRepo.deleteById(id);
        return ResponseEntity.ok("Response deleted successfully");
    }

    // ============================
    // ENUMS / UTILS
    // ============================

    @GetMapping("/responses/get_actions")
    public ResponseAction[] getResponseActions() {
        return ResponseAction.values();
    }
        
    @GetMapping("/get_vartypes")
    public VarType[] getVarTypes() {
        return VarType.values();
    }

    @GetMapping("/responses/match_modes")
    public MatchMode[] getMatchModes() {
        return MatchMode.values();
    }
}
