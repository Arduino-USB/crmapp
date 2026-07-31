package com.crm.entites;

import com.crm.embeds.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int dayMileStone;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<TextBlock> textObjs = new ArrayList<>();

	@JsonProperty("isBefore")
    private boolean isBefore; // true = x days before, false = x days after

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> responseIds = new ArrayList<>();

    public Message() {}

    public Message(Long id, List<TextBlock> textObjs, int dayMileStone, boolean isBefore, List<Long> responseIds) {
        this.id = id;
        this.textObjs = textObjs;
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


	public void addText(String text) {
		textObjs.add(new TextBlock(text));
	}

	public void addIfElse(TextBlock.VarType varType, String varName, List<String> ifText, Integer[] insertIndexes, String elseText) {
		textObjs.add(new TextBlock(varType, varName, ifText, insertIndexes, elseText));
	}
	
	public List<TextBlock> getTextList() {
		return this.textObjs;	
	}

	public void setTextList(List<TextBlock> textObjs) {
		this.textObjs = textObjs;	
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
