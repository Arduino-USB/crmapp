package com.crm.entites;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.HashMap;

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

	private Map<String, String> varibles = new HashMap<>();
    // Constructors
    public User() {
    }

    public User(Long id, String name, String phone, String prefName) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.prefName = prefName != null ? prefName : "";
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
		this.varibles.put(varName, varContent);
	}
	
	public String getVar(String varName) {
		this.varibles.get(varName);	
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
