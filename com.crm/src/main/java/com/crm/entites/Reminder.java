package com.crm.entites;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;



@Entity
@Table(name = "reminders") 
public class Reminder {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@NotNull
    private int dayMileStone;
    
	@NotBlank(message = "Name cannot be empty")
    private String name;

	@NotBlank(message = "Reminder cannot be empty")
	private String reminder;

	public int getDayMileStone() { return dayMileStone; }
	public void setDayMileStone(int dayMileStone) { this.dayMileStone = dayMileStone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

	public String getReminder() { return reminder; }
    public void setReminder(String reminder) { this.reminder = reminder; }



}
