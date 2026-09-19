package com.crm.entites;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reminders")
public class Reminder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private int dayMileStone;

	@Column(length = 4096)
	private String reminder;

	@ElementCollection(fetch = FetchType.EAGER)
	private List<Long> responseIds = new ArrayList<>();


	public Long getId() {
		return id;
	}

	public int getDayMileStone() {
		return dayMileStone;
	}

	public void setDayMileStone(int dayMileStone) {
		this.dayMileStone = dayMileStone;
	}

	public String getReminder() {
		return reminder;
	}

	public void setReminder(String reminder) {
		this.reminder = reminder;
	}

	public List<Long> getResponseIds() {
		return responseIds;
	}

	public void setResponseIds(List<Long> responseIds) {
		this.responseIds = responseIds;
	}

	public void addResponseId(Long responseId) {
		responseIds.add(responseId);
	}

	public void removeResponseId(Long responseId) {
		responseIds.remove(responseId);
	}
}
