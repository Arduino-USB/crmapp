package com.crm.entites;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;



@Entity
@Table(name = "users") // Maps this class to the "users" table in Postgres
public class User {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increments IDs. This ID is derived from Chart # in Dentrix
    private Long id;
    
	@NotBlank(message = "Name cannot be empty")
    private String name;

	private String phone;

	private String prefName = "";
    // Standard Boilerplate: Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

	public String getPrefName() { return prefName; }
    public void setPrefName(String prefName) { this.prefName = prefName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

}
