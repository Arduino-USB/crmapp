package com.crm.entites;

import com.crm.embeds.*;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;

import java.time.LocalDate;

@Entity
@Table(name = "users") 
public class User {

    @Id
    private Long id;

    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    private String memberId;

    private String primaryInsuranceName;
    private String primaryInsuranceId;

    private String secondaryInsuranceName;
    private String secondaryInsuranceId;

    private String phone;

    private String prefName = "";
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Varible> varibles = new ArrayList<>();

    private LocalDate dob;

    private String primarySubFirstName;
    private String primarySubLastName;
    private LocalDate primarySubDob;
    private String primarySubMemberId;

    private String secondarySubFirstName;
    private String secondarySubLastName;
    private LocalDate secondarySubDob;
    private String secondarySubMemberId;

	@OneToOne
	private Insurance primaryInsuranceFile;

	@OneToOne
	private Insurance secondaryInsuranceFile;
	

    // Constructors
    public User() {
    }

    public User(Long id, String firstName, String lastName, String phone, String prefName, List<Varible> varibles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
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

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getMemberId() { 
        return memberId; 
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPrimaryInsuranceName() { 
        return primaryInsuranceName; 
    }

    public void setPrimaryInsuranceName(String primaryInsuranceName) { 
        this.primaryInsuranceName = primaryInsuranceName; 
    }

    public String getPrimaryInsuranceId() { 
        return primaryInsuranceId; 
    }

    public void setPrimaryInsuranceId(String primaryInsuranceId) { 
        this.primaryInsuranceId = primaryInsuranceId; 
    }

    public String getSecondaryInsuranceName() { 
        return secondaryInsuranceName; 
    }

    public void setSecondaryInsuranceName(String secondaryInsuranceName) { 
        this.secondaryInsuranceName = secondaryInsuranceName; 
    }

    public String getSecondaryInsuranceId() { 
        return secondaryInsuranceId; 
    }

    public void setSecondaryInsuranceId(String secondaryInsuranceId) { 
        this.secondaryInsuranceId = secondaryInsuranceId; 
    }

    public String getFirstName() { 
        return firstName; 
    }

    public void setFirstName(String firstName) { 
        this.firstName = firstName; 
    }

    public String getLastName() { 
        return lastName; 
    }

    public void setLastName(String lastName) { 
        this.lastName = lastName; 
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

    public String getPrimarySubFirstName() {
        return primarySubFirstName;
    }

    public void setPrimarySubFirstName(String primarySubFirstName) {
        this.primarySubFirstName = primarySubFirstName;
    }

    public String getPrimarySubLastName() {
        return primarySubLastName;
    }

    public void setPrimarySubLastName(String primarySubLastName) {
        this.primarySubLastName = primarySubLastName;
    }

    public LocalDate getPrimarySubDob() {
        return primarySubDob;
    }

    public void setPrimarySubDob(LocalDate primarySubDob) {
        this.primarySubDob = primarySubDob;
    }

    public String getPrimarySubMemberId() {
        return primarySubMemberId;
    }

    public void setPrimarySubMemberId(String primarySubMemberId) {
        this.primarySubMemberId = primarySubMemberId;
    }

    public String getSecondarySubFirstName() {
        return secondarySubFirstName;
    }

    public void setSecondarySubFirstName(String secondarySubFirstName) {
        this.secondarySubFirstName = secondarySubFirstName;
    }

    public String getSecondarySubLastName() {
        return secondarySubLastName;
    }

    public void setSecondarySubLastName(String secondarySubLastName) {
        this.secondarySubLastName = secondarySubLastName;
    }

    public LocalDate getSecondarySubDob() {
        return secondarySubDob;
    }

    public void setSecondarySubDob(LocalDate secondarySubDob) {
        this.secondarySubDob = secondarySubDob;
    }

    public String getSecondarySubMemberId() {
        return secondarySubMemberId;
    }

    public void setSecondarySubMemberId(String secondarySubMemberId) {
        this.secondarySubMemberId = secondarySubMemberId;
    }

	public Insurance getPrimaryInsuranceFile() {
		return primaryInsuranceFile;
	}

	public void setPrimaryInsuranceFile(Insurance primaryInsuranceFile) {
		this.primaryInsuranceFile = primaryInsuranceFile;
	}

	public Insurance getSecondaryInsuranceFile() {
		return secondaryInsuranceFile;
	}

	public void setSecondaryInsuranceFile(Insurance secondaryInsuranceFile) {
		this.secondaryInsuranceFile = secondaryInsuranceFile;
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
    }
}
