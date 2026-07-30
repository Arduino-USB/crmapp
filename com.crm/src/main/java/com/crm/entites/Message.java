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
	
	public enum VarType {
		USER_VAR,
		APPOINTMENT_VAR
	}

	public class TextBlock {
		public TextBlock(String text) {
			this.text = text;		
		}
		


		public String text;
	}
		
	public class IfElseBlock {	
		
		public IfElseBlock (VarType varType, String varName, List<String> ifText, Integer[] insertIndexes,String elseText) {
			this.varType = varType;
			this.varName = varName;
			this.ifText = ifText;
			this.elseText = elseText;	
			this.insertIndexes = insertIndexes;
		}	
		

		public VarType varType;
		public String varName;
		public List<String> ifText;
		public Integer[] insertIndexes;
		public String elseText;
	}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;

    private int dayMileStone;
	
	
	private List<Object> textObjs = new ArrayList<>();

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


	public void addText(String text) {
		textObjs.add(new TextBlock(text));
	}

	public void addIfElse(VarType varType, String varName, String ifText, String elseText) {
		textObjs.add(new IfElseBlock(varType, varName, ifText, elseText));
	}
	
	public List<Object> getTextList() {
		return this.textObjs;	
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
