package com.crm.entites;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private int dayMileStone;

	@JsonProperty("isBefore")
    private boolean isBefore; // true = x days before, false = x days after

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> responseIds = new ArrayList<>();

    public Message() {}

    public Message(Long id, String text, int dayMileStone, boolean isBefore, List<Long> responseIds) {
        this.id = id;
        this.text = text;
        this.dayMileStone = dayMileStone;
        this.isBefore = isBefore;
        if (responseIds != null) {
            this.responseIds = responseIds;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDayMileStone() {
        return dayMileStone;
    }

    public void setDayMileStone(int dayMileStone) {
        this.dayMileStone = dayMileStone;
    }

    public boolean isBefore() {
        return isBefore;
    }

    public void setBefore(boolean isBefore) {
        this.isBefore = isBefore;
    }

    public List<Long> getResponseIds() {
        return responseIds;
    }

    public void setResponseIds(List<Long> responseIds) {
        this.responseIds = responseIds;
    }
}
