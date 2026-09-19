package com.crm.entites;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sms_logs")
public class SmsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long appointmentId;
    private Long userId;
    private String recipientPhone;
    private String direction; // "OUTBOUND" or "INBOUND"
    
    @Column(columnDefinition = "TEXT")
    private String messageContent;
    
    private Long externalTextId; // Maps to textMgr textId / currentTextId
    private Instant timestamp;

    public SmsLog() {}

    public SmsLog(Long appointmentId, Long userId, String recipientPhone, String direction, String messageContent, Long externalTextId) {
        this.appointmentId = appointmentId;
        this.userId = userId;
        this.recipientPhone = recipientPhone;
        this.direction = direction;
        this.messageContent = messageContent;
        this.externalTextId = externalTextId;
        this.timestamp = Instant.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRecipientPhone() { return recipientPhone; }
    public void setRecipientPhone(String recipientPhone) { this.recipientPhone = recipientPhone; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public Long getExternalTextId() { return externalTextId; }
    public void setExternalTextId(Long externalTextId) { this.externalTextId = externalTextId; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
