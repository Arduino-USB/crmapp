package com.crm.entites;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import java.time.Instant;

import java.util.List;

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
		
	private boolean fromJson = false;

	@ElementCollection
	private List<Integer> mileStoneList;	
	private Instant lastTextSent; 
	
	

	private String reason = "";
    public Long getId() { return id; }

	public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Instant getTime() { return time; }
    public void setTime(Instant time) { this.time = time; }
	
    public Instant lastTextSent() { return lastTextSent; }
    public void setTextSentNow() { this.lastTextSent = Instant.now(); }

	public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }


	public List<Integer> getMileStoneList() { return mileStoneList; }
    public void addMileStoneToList(int milestone) { mileStoneList.add(milestone); }

	public void removeMileStoneToList(int milestone) { 
	    if (this.mileStoneList != null) {
	        this.mileStoneList.removeIf(m -> m.equals(milestone)); 
	    }
	}

	public boolean fromJson() { return fromJson; }
    public void setJsonStatus(boolean fromJson) { this.fromJson = fromJson; }

}
