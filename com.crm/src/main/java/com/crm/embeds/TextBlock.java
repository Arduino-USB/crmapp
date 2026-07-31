package com.crm.embeds;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

import java.util.List;

@Embeddable
public class TextBlock {
	
	public enum VarType {
		USER_VAR,
		APPOINTMENT_VAR
	}
	
	public enum Mode {
		TEXT,
		IFELSE	
	}
	
	public TextBlock() {
	
	}

	public TextBlock(String text) {
		this.plainText = text;
		this.mode = Mode.TEXT;
	}

	public TextBlock (VarType varType, String varName, List<String> ifText, Integer[] insertIndexes,String elseText) {
		this.varType = varType;
		this.varName = varName;
		this.ifText = ifText;
		this.elseText = elseText;	
		this.insertIndexes = insertIndexes;
		this.mode = Mode.IFELSE;
	}	
	

	public VarType varType;
	public String varName;
	public List<String> ifText;
	public Integer[] insertIndexes;
	public String elseText;
	public String plainText;
	public Mode mode;
}
