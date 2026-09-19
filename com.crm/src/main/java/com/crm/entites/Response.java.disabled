package com.crm.entites;

import com.crm.enums.MatchMode;
import com.crm.enums.ResponseAction;

import jakarta.persistence.*;

@Entity
public class Response {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private MatchMode mode;

	private String text;

	@Column(length = 4096)
	private String response;

	@Enumerated(EnumType.STRING)
	private ResponseAction action;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MatchMode getMode() {
		return mode;
	}

	public void setMode(MatchMode mode) {
		this.mode = mode;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public ResponseAction getAction() {
		return action;
	}

	public void setAction(ResponseAction action) {
		this.action = action;
	}
}
