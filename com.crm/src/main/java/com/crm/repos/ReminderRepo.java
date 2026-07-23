package com.crm.repos;

import com.crm.entites.Reminder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.List;

@Repository
public interface ReminderRepo extends JpaRepository<Reminder, Long> {

	List<Reminder> findAll();    
	Optional<Reminder> findByDayMileStone(int dayMileStone);

	
	
}
