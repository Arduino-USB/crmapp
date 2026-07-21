package com.crm.entites;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.time.Instant;


@Entity
@Table(name = "appointments") 
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments IDs
    private Long id;

    
	@NotNull(message = "User id cannot be empty")
    private Long userId;

	@NotNull(message = "Time cannot be empty")
	private Instant time;
    // Standard Boilerplate: Getters and Setters
		
	private boolean fromCsvFile = false;

	private String reason = "";
    public Long getId() { return id; }

	public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
	
	public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
		
	public boolean fromCsvFile() { return fromCsvFile; }
    public void setCsvStatus(boolean fromCsvFile) { this.fromCsvFile = fromCsvFile; }

}
