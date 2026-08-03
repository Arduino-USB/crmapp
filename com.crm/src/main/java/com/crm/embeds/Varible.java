package com.crm.embeds;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

@Embeddable
public class Varible {
	
	public Varible() {

	}

	public Varible(String varName, String varContents) {
		this.varName = varName;
		this.varContents = varContents;
	}
	

	public String varName;
	public String varContents;	
}	

