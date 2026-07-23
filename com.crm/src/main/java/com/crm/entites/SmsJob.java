package com.crm.entites;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sms_jobs")
public class SmsJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant createdAt;
    private Instant updatedAt;
    private Long sentAt; // Epoch millis timestamp when status became SENT

    public enum Status {
        PENDING,
        PROCESSING,
        SENT,
        FAILED
    }

    public SmsJob() {}

    public SmsJob(String recipient, String text) {
        this.recipient = recipient;
        this.text = text;
        this.status = Status.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getRecipient() { return recipient; }
    public String getText() { return text; }
    public Status getStatus() { return status; }
    public Long getSentAt() { return sentAt; }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }
}
