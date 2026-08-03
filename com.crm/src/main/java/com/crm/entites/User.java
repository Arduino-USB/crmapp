package com.crm.entites;

import com.crm.embeds.*;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "users") // Maps this class to the "users" table in Postgres
public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) // Derived from Chart # in Dentrix
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    private String phone;

    private String prefName = "";
	
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<Varible> varibles = new ArrayList<>();

    // Constructors
    public User() {
    }

    public User(Long id, String name, String phone, String prefName, List<Varible> varibles) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.prefName = prefName != null ? prefName : "";
		this.varibles = varibles;
    }

    // Getters and Setters
    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public String getName() { 
        return name; 
    }

    public void setName(String name) { 
        this.name = name; 
    }

    public String getPrefName() { 
        return prefName; 
    }

    public void setPrefName(String prefName) { 
        this.prefName = prefName != null ? prefName : ""; 
    }

    public String getPhone() { 
        return phone; 
    }

    public void setPhone(String phone) { 
        this.phone = phone; 
    }

	public void setVar(String varName, String varContent) {
		for (int i = 0; i < varibles.size(); i++) {
			Varible currentVar = varibles.get(i);
			if (currentVar.varName.equals(varName)) {
				currentVar.varContents = varContent;	
				return;
			}
		}
		
		varibles.add(new Varible(varName, varContent));
		//this.varibles.put(varName, varContent);
	}
	
	public void setVarList(List<Varible> varibles) {
		
		this.varibles = varibles;
	}

	public List<Varible> getVarList() {
		
		return this.varibles;
	}


	public String getVar(String varName) {
		for (int i = 0; i < varibles.size(); i++) {
			Varible currentVar = varibles.get(i);
			if (currentVar.varName.equals(varName)) {  	
				return currentVar.varContents;
			}
		
		}
		
		return null;
		//this.varibles.get(varName);	
	}

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", prefName='" + prefName + '\'' +
                '}';
    }
}
