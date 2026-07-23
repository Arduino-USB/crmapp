package com.crm.entites;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "appointments")
public class Appointment {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@NotNull(message = "User id cannot be empty")
	private Long userId;


	@NotNull(message = "Time cannot be empty")
	private Instant time;


	private boolean fromJson = false;

	private Long currentReminderId;

	@Column(nullable = false, columnDefinition = "boolean default true")
	private boolean waitingForResponse = true;


	@ElementCollection(fetch = FetchType.EAGER)
	private List<Integer> mileStoneList = new ArrayList<>();


	private Instant lastTextSent;

	private Long currentTextId;

	private String reason = "";


	public Long getId() {
		return id;
	}


	public Long getCurrentTextId() {
		return currentTextId;
	}

	public void setCurrentTextId(Long currentTextId) {
		this.currentTextId = currentTextId;
	}


	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}


	public Instant getLastTextSent() {
		return lastTextSent;
	}

	public void setTextSentNow() {
		this.lastTextSent = Instant.now();
	}


	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}


	public List<Integer> getMileStoneList() {
		return mileStoneList;
	}


	public void addMileStoneToList(int milestone) {
		if (mileStoneList == null) {
			mileStoneList = new ArrayList<>();
		}

		mileStoneList.add(milestone);
	}


	public void removeMileStoneFromList(int milestone) {
		if (mileStoneList != null) {
			mileStoneList.removeIf(m -> m.equals(milestone));
		}
	}


	public boolean fromJson() {
		return fromJson;
	}

	public void setJsonStatus(boolean fromJson) {
		this.fromJson = fromJson;
	}


	public boolean isWaitingForResponse() {
		return waitingForResponse;
	}

	public void setWaitingForResponse(boolean waitingForResponse) {
		this.waitingForResponse = waitingForResponse;
	}


	public Long getCurrentReminderId() {
		return currentReminderId;
	}

	public void setCurrentReminderId(Long currentReminderId) {
		this.currentReminderId = currentReminderId;
	}

}
